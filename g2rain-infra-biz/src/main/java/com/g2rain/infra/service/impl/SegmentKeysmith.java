package com.g2rain.infra.service.impl;


import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Strings;
import com.g2rain.infra.dao.G2rainRaindropDao;
import com.g2rain.infra.dao.po.G2rainRaindropPo;
import com.g2rain.infra.dto.G2rainRaindropSelectDto;
import com.g2rain.infra.enums.InfraErrorCode;
import com.g2rain.infra.enums.KeysmithType;
import com.g2rain.infra.model.Segment;
import com.g2rain.infra.model.SegmentBuffer;
import com.g2rain.infra.service.Keysmith;
import com.g2rain.infra.utils.Constants;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <h1>SegmentKeysmith</h1>
 *
 * <p>分布式号段 ID 生成器，实现 Keysmith 接口，基于双缓冲 SegmentBuffer + Segment
 * 设计，实现高并发安全的业务唯一 ID 分配。</p>
 *
 * <h2>核心特性</h2>
 * <ul>
 *     <li>懒加载：首次请求业务标签时初始化 SegmentBuffer，避免启动时批量加载</li>
 *     <li>双缓冲策略：SegmentBuffer 内维护 current 与 next 两个 Segment，实现无阻塞切换</li>
 *     <li>异步预加载：当当前 Segment 使用量超过 10% 时，异步刷新下一个 Segment，保证号段连续性</li>
 *     <li>线程安全：AtomicLong 保证 ID 自增原子性，读写锁保证号段切换和多线程取号安全</li>
 *     <li>动态步长调整：根据号段使用时间自动调整步长，兼顾性能与号段利用率</li>
 *     <li>缓存管理：定时刷新业务标签，新增标签动态加入缓存，失效标签清理，保证内存数据与数据库一致</li>
 *     <li>自旋等待：当下一个 Segment 未就绪时，使用轻量自旋 + 短暂休眠等待刷新完成，降低阻塞开销</li>
 * </ul>
 *
 * <h2>业务适用场景</h2>
 * <ul>
 *     <li>多节点分布式系统高并发业务 ID 分配</li>
 *     <li>单节点号段连续性保证，同时允许跨节点跳号</li>
 *     <li>业务标签动态变化场景，支持标签的新增和删除</li>
 * </ul>
 *
 * <h2>主要流程</h2>
 * <ol>
 *     <li>初始化：服务启动或 @Scheduled 定时任务加载数据库业务标签并初始化缓存</li>
 *     <li>取号逻辑：
 *         <ol type="a">
 *             <li>首次取号检查 SegmentBuffer 是否初始化，未初始化则刷新当前 Segment 并标记 initOk</li>
 *             <li>获取当前 Segment 并尝试自增获取 ID</li>
 *             <li>异步预加载下一个 Segment，当当前号段使用量超过 10% 时触发</li>
 *             <li>当前 Segment 无号可取且下一个 Segment 已就绪时，切换到 next Segment，并标记 nextReady = false</li>
 *             <li>当前 Segment 无号且下一个 Segment 未就绪，自旋 + 短暂休眠等待刷新完成</li>
 *         </ol>
 *     </li>
 *     <li>步长调整：根据当前号段使用时间与预设最大步长调整 nextStep，避免号段浪费或频繁刷新数据库</li>
 * </ol>
 *
 * <h2>异常处理</h2>
 * <ul>
 *     <li>未初始化缓存或业务标签不存在，抛出 IllegalStateException</li>
 *     <li>下一个 Segment 未就绪且当前 Segment 用尽，抛出 BusinessException</li>
 *     <li>数据库操作失败，抛出 BusinessException</li>
 * </ul>
 *
 * <h2>属性说明</h2>
 * <ul>
 *     <li>{@link #g2rainRaindropDao} - 数据库 DAO，用于获取业务号段记录</li>
 *     <li>{@link #MAX_STEP} - 单号段最大步长</li>
 *     <li>{@link #SEGMENT_DURATION} - 单号段维持时间（毫秒）</li>
 *     <li>{@link #cache} - 内存缓存，每个业务标签对应 SegmentBuffer</li>
 *     <li>{@link #service} - 异步线程池，用于刷新下一个 Segment</li>
 * </ul>
 *
 * <h2>方法说明</h2>
 * <ul>
 *     <li>{@link #refresh()} - 初始化缓存，从数据库加载业务标签并初始化 SegmentBuffer</li>
 *     <li>{@link #allocate(String)} - 获取指定业务标签的唯一 ID，含懒加载、异步预加载、号段切换逻辑</li>
 *     <li>{@link #obtainIdWithPrefetch(SegmentBuffer)} - 核心取号方法，处理当前号段取号、异步预加载、切换 Segment</li>
 *     <li>{@link #prefetchNextSegmentIfNeeded(SegmentBuffer, Segment)} - 异步刷新下一个 Segment</li>
 *     <li>{@link #waitAndSleep(SegmentBuffer)} - 自旋 + 短暂休眠等待下一个 Segment 刷新完成</li>
 *     <li>{@link #refreshSegmentRecord(String, Segment)} - 刷新 Segment 号段数据并动态调整步长</li>
 *     <li>{@link #updateMaxIdAndGetRaindrop(String)} - 数据库更新号段最大值并获取记录</li>
 *     <li>{@link #updateMaxIdByCustomStepAndGetRaindrop(String, Integer)} - 按自定义步长更新号段并获取记录</li>
 *     <li>{@link #getRaindrop(String)} - 查询数据库获取指定业务号段记录</li>
 * </ul>
 *
 * <p>该类综合了懒加载、双缓冲、异步预加载、动态步长和线程安全设计，适合高并发分布式业务 ID 生成场景</p>
 *
 * @author alpha
 * @since 2025/12/25
 */
@Slf4j
@Service(value = "segmentKeysmith")
public class SegmentKeysmith implements Keysmith {

    /**
     * 单号段最大步长，防止号段过大造成浪费
     */
    private static final int MAX_STEP = 1_000_000;

    /**
     * 步长调整时间阈值（毫秒），默认 15 分钟。
     *
     * <p>用于判断当前号段使用时长，从而动态调整下一号段的步长：</p>
     * <ul>
     *     <li>使用时长 &lt; 15 分钟 → 步长翻倍（若未超过 MAX_STEP）</li>
     *     <li>15 分钟 ≤ 使用时长 &lt; 30 分钟 → 步长保持不变</li>
     *     <li>使用时长 ≥ 30 分钟 → 步长减半（若不低于 minStep）</li>
     * </ul>
     */
    private static final long SEGMENT_DURATION = 15 * 60 * 1000L;

    /**
     * 内存缓存，每个业务标签对应一个 SegmentBuffer
     */
    private final Map<String, SegmentBuffer> cache = new ConcurrentHashMap<>();

    /**
     * 异步任务线程池，用于预加载下一个 Segment
     */
    private final ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * 单线程定时任务执行器，用于定期刷新业务标签缓存
     *
     * <p>说明：</p>
     * 1. 使用单线程保证缓存刷新顺序，避免并发刷新导致脏数据。
     * 2. 线程名指定为 "SegmentKeysmith-CacheUpdater"，方便日志和线程分析。
     * 3. 线程设置为守护线程（daemon），JVM 退出时自动结束，不会阻塞进程关闭。
     */
    ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(r -> {
        // 指定线程名
        Thread thread = new Thread(r, "SegmentKeysmith-CacheUpdater");
        // 守护线程，随JVM退出自动结束
        thread.setDaemon(true);
        return thread;
    });

    /**
     * 数据库访问 DAO，用于获取业务号段记录
     */
    @Resource(name = "g2rainRaindropDao")
    private G2rainRaindropDao g2rainRaindropDao;

    @Override
    public KeysmithType type() {
        return KeysmithType.SEGMENT;
    }

    /**
     * 服务启动后初始化缓存
     * <p>加载数据库业务标签并初始化对应 SegmentBuffer</p>
     */
    @PostConstruct
    public void refresh() {
        refreshCacheFromDatabase();

        /*
         * 定时刷新缓存，保证数据库新增/删除的业务标签同步到内存
         * <p>执行频率为 60 秒一次</p>
         */
        ses.scheduleWithFixedDelay(
            this::refreshCacheFromDatabase,
            60, 60, TimeUnit.SECONDS
        );
    }

    /**
     * 刷新内存缓存：
     * <ul>
     *     <li>新增标签：初始化 SegmentBuffer 并设置默认值</li>
     *     <li>删除标签：从缓存中移除</li>
     * </ul>
     */
    private void refreshCacheFromDatabase() {
        log.info("Refresh cache from database.");

        try {
            List<String> bizTags = g2rainRaindropDao.selectAllBizTags();
            if (Collections.isEmpty(bizTags)) {
                return;
            }

            Set<String> lookup = new HashSet<>(bizTags);
            for (String bizTag : lookup) {
                this.cache.computeIfAbsent(bizTag, t -> {
                    SegmentBuffer buffer = new SegmentBuffer();
                    buffer.setTag(t);
                    Segment segment = buffer.getCurrent();
                    segment.setMax(0);
                    segment.setStep(0);
                    log.info("Add tag: {} from dataBase to cache, SegmentBuffer:{}",
                        t, buffer
                    );
                    return buffer;
                });
            }

            // 删除数据库已失效标签
            this.cache.keySet().removeIf(key ->
                !lookup.contains(key)
            );
        } catch (Exception e) {
            log.warn("refresh cache from database exception", e);
        }
    }

    /**
     * 获取业务标签的唯一 ID
     *
     * @param bizTag 业务标签
     * @return 分配的唯一 ID
     */
    @Override
    public long allocate(String bizTag) {
        // 如果不传值 设置一个默认值
        if (Strings.isBlank(bizTag)) {
            bizTag = Constants.DEFAULT_BIZ_TAG;
        }

        // 没有对应的业务号段标签, 直接异常
        SegmentBuffer buffer = cache.get(bizTag);
        if (Objects.isNull(buffer)) {
            throw new BusinessException(
                InfraErrorCode.BIZ_TAG_NOT_FOUND
            );
        }

        // 懒加载首次初始化 SegmentBuffer
        if (!buffer.isInitOk()) {
            synchronized (buffer) {
                if (!buffer.isInitOk()) {
                    try {
                        // 刷新当前的 Segment
                        Segment segment = buffer.getCurrent();
                        refreshSegmentRecord(bizTag, segment);
                        log.info("Init buffer. Update tag: {}, {} from database",
                            bizTag, buffer.getCurrent()
                        );
                        // 刷新完毕后, 设置初始化完毕
                        buffer.setInitOk(true);
                    } catch (Exception e) {
                        log.warn("Init buffer {} exception",
                            buffer.getCurrent(), e
                        );
                    }
                }
            }
        }

        return obtainIdWithPrefetch(buffer);
    }

    /**
     * 核心取号方法
     * <p>包括当前 Segment 自增获取 ID、异步预加载下一个 Segment、切换 Segment</p>
     *
     * @param buffer 业务标签对应的 SegmentBuffer
     * @return 分配的唯一 ID
     */
    private long obtainIdWithPrefetch(final SegmentBuffer buffer) {
        while (true) {
            // 读锁保证取号安全
            buffer.rLock().lock();
            try {
                // 获取当前的 Segment
                Segment segment = buffer.getCurrent();
                prefetchNextSegmentIfNeeded(buffer, segment);
                // 尝试获取 ID
                long value = segment.getValue().getAndIncrement();
                if (value < segment.getMax()) {
                    return value;
                }
            } finally {
                buffer.rLock().unlock();
            }

            // 当前号段无号可取，自旋等待
            waitAndSleep(buffer);

            // 再次取号，并可能切换到下一个 Segment
            buffer.wLock().lock();
            try {
                // 取号
                final Segment segment = buffer.getCurrent();
                long value = segment.getValue().getAndIncrement();
                if (value < segment.getMax()) {
                    return value;
                }

                // 当前得 Segment 没有号, 但是下一个 Segment 还没准备好, 直接异常
                if (!buffer.isNextReady()) {
                    log.error("Both two segments in {} are not ready!", buffer);
                    throw new BusinessException(InfraErrorCode.SEGMENT_NOT_READY);
                }

                /*
                 * 切换 Segment
                 * 并标识下一个 Segment 没准备好, 因为刚用完, 待重新发号号段
                 * 继续轮训, 直到取到号为止
                 */
                buffer.switchPos();
                buffer.setNextReady(false);
            } finally {
                buffer.wLock().unlock();
            }
        }
    }

    /**
     * 异步预加载下一个 Segment
     * <p>当当前 Segment 使用量超过 10% 时触发，保证号段连续性</p>
     *
     * @param buffer  业务标签对应的 SegmentBuffer
     * @param segment 当前使用的 Segment
     */
    private void prefetchNextSegmentIfNeeded(SegmentBuffer buffer, Segment segment) {
        // 下一个 Segment 已经准备好, 不需要预取
        if (buffer.isNextReady()) {
            return;
        }

        // 当前的 Segment 消耗的的序列没有超过 10%, 不需要预取
        if (segment.getIdle() >= 0.9 * segment.getStep()) {
            return;
        }

        // 尝试获取预加载执行权，如果已有线程在执行则直接返回
        if (!buffer.getThreadRunning().compareAndSet(false, true)) {
            return;
        }

        service.execute(() -> {
            // 获取另一个 Segment
            Segment next = buffer.getSegments()[buffer.nextPos()];
            boolean updateOk = false;
            try {
                // 对另外一个 Segment 刷新缓存并更新数据库
                refreshSegmentRecord(buffer.getTag(), next);
                updateOk = true;
                log.info("update segment {} from db {}", buffer.getTag(), next);
            } catch (Exception e) {
                log.warn("{} refreshSegmentRecord exception", buffer.getTag(), e);
            } finally {
                if (updateOk) {
                    // 加锁修改下一个 Segment 已经准备好
                    buffer.wLock().lock();
                    buffer.setNextReady(true);
                    // 只要设置另外一个 Segment, 需要标识当前线程执行完毕
                    buffer.getThreadRunning().set(false);
                    buffer.wLock().unlock();
                } else {
                    // 只要设置另外一个 Segment, 需要标识当前线程执行完毕
                    buffer.getThreadRunning().set(false);
                }
            }
        });
    }

    /**
     * 自旋 + 短暂休眠等待下一个 Segment 刷新完成
     *
     * @param buffer SegmentBuffer
     */
    private void waitAndSleep(SegmentBuffer buffer) {
        int roll = 0;
        while (buffer.getThreadRunning().get()) {
            roll += 1;
            if (roll > 10_000) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    // 恢复中断状态（推荐）
                    Thread.currentThread().interrupt();
                    log.warn("Thread {} Interrupted",
                        Thread.currentThread().getName()
                    );
                }
                break;
            } else {
                // 告诉 CPU 当前在忙等, 可能减少功耗
                Thread.onSpinWait();
            }
        }
    }

    /**
     * 刷新 Segment 数据并动态调整步长
     *
     * @param bizTag  业务标签
     * @param segment Segment 对象
     */
    private void refreshSegmentRecord(String bizTag, Segment segment) {
        SegmentBuffer buffer = segment.getBuffer();
        G2rainRaindropPo raindrop;
        // 没有初始化, 根据默认step计算maxId然后获取记录
        if (!buffer.isInitOk() || buffer.getUpdateTimestamp() == 0) {
            raindrop = updateMaxIdAndGetRaindrop(bizTag);
            // Database 的 step 作为 Segment 中的 step
            buffer.setMinStep(raindrop.getStep());
            buffer.setStep(raindrop.getStep());
            buffer.setUpdateTimestamp();
        } else {
            long now = System.currentTimeMillis();
            long duration = now - buffer.getUpdateTimestamp();
            int nextStep = buffer.getStep();
            if (duration < SEGMENT_DURATION) {
                // 15m内 没超上限, 步长 * 2; 超过上限保持现状
                if (nextStep * 2 > MAX_STEP) {
                    log.debug("nextStep Limit exceeded: {}", MAX_STEP);
                } else {
                    nextStep = nextStep * 2;
                }
            } else if (duration < SEGMENT_DURATION * 2) {
                // 15m - 30m, 什么都不做
                log.debug("Do not scale nextStep for 15–30 minutes");
            } else {
                // > 30m 不超下限, 缩小一半; 否则保持现状
                int halfStep = nextStep / 2;
                nextStep = halfStep >= buffer.getMinStep() ? halfStep : nextStep;
            }

            raindrop = updateMaxIdByCustomStepAndGetRaindrop(bizTag, nextStep);
            buffer.setMinStep(raindrop.getStep());
            buffer.setStep(nextStep);
            buffer.setUpdateTimestamp();
        }

        segment.getValue().set(raindrop.getMaxId() - buffer.getStep());
        segment.setMax(raindrop.getMaxId());
        segment.setStep(buffer.getStep());
    }

    /**
     * 更新数据库号段最大值并获取 Raindrop 记录
     *
     * @param bizTag 业务标签
     * @return Raindrop 数据对象
     * @throws BusinessException 如果更新失败
     */
    private G2rainRaindropPo updateMaxIdAndGetRaindrop(String bizTag) {
        if (g2rainRaindropDao.updateMaxId(bizTag) == 0) {
            throw new BusinessException(InfraErrorCode.SEGMENT_UPDATE_FAILED);
        }

        return getRaindrop(bizTag);
    }

    /**
     * 按自定义步长更新数据库号段最大值并获取 Raindrop 记录
     *
     * @param bizTag 业务标签
     * @param step   步长
     * @return Raindrop 数据对象
     * @throws BusinessException 如果更新失败
     */
    private G2rainRaindropPo updateMaxIdByCustomStepAndGetRaindrop(String bizTag, Integer step) {
        if (g2rainRaindropDao.updateMaxIdByCustomStep(bizTag, step) == 0) {
            throw new BusinessException(InfraErrorCode.SEGMENT_UPDATE_FAILED);
        }

        return getRaindrop(bizTag);
    }

    /**
     * 查询数据库获取指定业务号段记录
     *
     * @param bizTag 业务标签
     * @return Raindrop 数据对象
     * @throws BusinessException 如果查询为空
     */
    private G2rainRaindropPo getRaindrop(String bizTag) {
        G2rainRaindropSelectDto selectDto = new G2rainRaindropSelectDto();
        selectDto.setBizTag(bizTag);
        List<G2rainRaindropPo> raindrops = g2rainRaindropDao.selectList(selectDto);
        if (Collections.isEmpty(raindrops)) {
            throw new BusinessException(InfraErrorCode.SEGMENT_NOT_FOUND);
        }

        return raindrops.getFirst();
    }
}
