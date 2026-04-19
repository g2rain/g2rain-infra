package com.g2rain.infra.service.impl;


import com.g2rain.common.exception.BusinessException;
import com.g2rain.infra.components.RedisWorkerIdManager;
import com.g2rain.infra.enums.InfraErrorCode;
import com.g2rain.infra.enums.KeysmithType;
import com.g2rain.infra.service.Keysmith;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <h1>SnowflakeKeysmith</h1>
 *
 * <p>分布式唯一 ID 生成器，基于 Twitter Snowflake 算法，保证高并发环境下 ID 全局唯一性。</p>
 *
 * <h2>功能概述</h2>
 * <ul>
 *     <li>64-bit long 类型全局唯一 ID</li>
 *     <li>保证单节点时间递增，多节点避免冲突</li>
 *     <li>高并发环境每毫秒生成数千 ID</li>
 * </ul>
 *
 * <h2>核心设计思想</h2>
 * <ul>
 *     <li>节点唯一性：workerId 由 RedisWorkerIdManager 分配，确保集群唯一</li>
 *     <li>时间戳高位：41 位表示相对纪元的毫秒偏移</li>
 *     <li>节点中位：10 位 workerId 区分不同节点，最大值 1023</li>
 *     <li>序列号低位：12 位毫秒内序列号，缓解同毫秒冲突</li>
 *     <li>随机序列初始化：缓解序列号热点，序列号溢出从随机点开始</li>
 *     <li>时钟回拨防护：检测系统时间回退，避免生成重复 ID</li>
 *     <li>CPU hint 优化：Thread.onSpinWait() 减少忙等 CPU 消耗</li>
 * </ul>
 *
 * <h2>防护点</h2>
 * <ul>
 *     <li>时钟回拨 &gt;5ms，直接抛异常</li>
 *     <li>序列号溢出处理：循环 + 随机起点</li>
 *     <li>线程安全：get() 方法 synchronized</li>
 *     <li>workerId 自动续租，保证节点唯一性</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * @Autowired
 * private SnowflakeKeysmith keysmith;
 *
 * public void example() {
 *     long id = keysmith.get("order");
 *     System.out.println("生成唯一 ID: " + id);
 * }
 * }</pre>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>workerId 必须在 [0, 1023] 范围内，否则抛异常</li>
 *     <li>纪元时间必须早于系统时间，否则 ID 异常</li>
 *     <li>多节点环境必须保证 workerId 唯一</li>
 * </ul>
 *
 * @author alpha
 * @since 2025/12/25
 */
@Slf4j
@Service(value = "snowflakeKeysmith")
public class SnowflakeKeysmith implements Keysmith {

    /**
     * Redis WorkerId 管理器，负责 workerId 分配、续租、释放
     */
    private final RedisWorkerIdManager redisWorkerIdManager;

    /**
     * 随机数生成器，用于序列号起点随机化
     */
    private static final Random RANDOM = new Random();

    /**
     * 序列号位数
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 序列号掩码，保证 12 位不溢出，二进制 0b111111111111
     */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /**
     * workerId 位数
     */
    private static final long WORKER_ID_BITS = 10L;

    /**
     * workerId 左移位数，放在序列号高位
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 时间戳左移位数，放在 workerId+序列号左边
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 当前节点 workerId
     */
    private long workerId = -1;

    /**
     * 自定义纪元，单位毫秒: 2026-01-01 00:00:00 UTC
     */
    private static final long EPOCH_START = 1767225600000L;

    /**
     * 当前毫秒内序列号
     */
    private long sequence = 0L;

    /**
     * 上次生成 ID 的时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 单线程定时任务执行器，用于定期续租workerId
     *
     * <p>说明：</p>
     * 1. 线程名指定为 "SnowflakeKeysmith-LeaseRenewer"，方便日志和线程分析。
     * 2. 线程设置为守护线程（daemon），JVM 退出时自动结束，不会阻塞进程关闭。
     */
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "SnowflakeKeysmith-LeaseRenewer");
        // 守护线程，避免阻塞 JVM 退出
        t.setDaemon(true);
        return t;
    });

    /**
     * 当前 workerId 是否有效，false 表示需重新申请
     */
    private volatile boolean workerIdValid = false;

    /**
     * 连续续租失败次数计数器
     */
    private int failCount = 0;

    /**
     * 最大连续续租失败次数
     */
    private static final int MAX_FAIL = 3;

    /**
     * 构造方法
     *
     * @param redisWorkerIdManager 雪花算法 WorkerId 管理器
     */
    public SnowflakeKeysmith(RedisWorkerIdManager redisWorkerIdManager) {
        this.redisWorkerIdManager = redisWorkerIdManager;
    }

    @Override
    public KeysmithType type() {
        return KeysmithType.SNOWFLAKE;
    }

    /**
     * 服务启动后自动抢号并初始化 workerId
     */
    @PostConstruct
    public void start() {
        try {
            // 抢 workerId
            this.workerId = redisWorkerIdManager.acquireWorkerId();
        } catch (Exception e) {
            throw new BusinessException(InfraErrorCode.SNOWFLAKE_WORKER_ID_FAIL);
        }

        if (this.workerId == -1) {
            throw new BusinessException(InfraErrorCode.SNOWFLAKE_WORKER_ID_FAIL);
        }

        // 抢号成功，标记有效
        workerIdValid = true;
        log.debug("雪花算法的取号器申到 WorkerId: {}", this.workerId);

        scheduledExecutor.scheduleWithFixedDelay(
            this::scheduledUpdateCache,
            10, 10, TimeUnit.SECONDS
        );
    }

    /**
     * 定时心跳续租 workerId
     * 每 10 秒续租一次
     */
    public void scheduledUpdateCache() {
        if (!workerIdValid) {
            try {
                // workerId 无效，尝试重新抢号
                long newWorkerId = redisWorkerIdManager.acquireWorkerId();
                if (newWorkerId == -1) {
                    log.warn("重新申请 workerId 失败");
                    return;
                }

                this.workerId = newWorkerId;
                workerIdValid = true;
                failCount = 0;
                log.debug("重新申请到 workerId: {}", this.workerId);
            } catch (Exception e) {
                // 如果网络出现异常
                log.error("重新申请 workerId 失败", e);
            }

            return;
        }

        try {
            boolean ok = redisWorkerIdManager.renewLease(this.workerId);
            if (ok) {
                // 成功续租，重置 failCount
                failCount = 0;
                log.debug("workerId {} 续租成功", this.workerId);
            } else {
                // 续租失败，增加 failCount
                failCount++;
                log.warn("workerId {} 续租失败, failCount={}", this.workerId, failCount);
                if (failCount >= MAX_FAIL) {
                    // 连续失败达到阈值，标记 workerId 无效
                    workerIdValid = false;
                    log.warn("workerId {} 连续 {} 次续租失败，标记为无效", this.workerId, MAX_FAIL);
                }
            }
        } catch (Exception e) {
            // 网络抖动或 Redis 异常也算续租失败
            failCount++;
            log.error("workerId {} 续租异常, failCount={}", this.workerId, failCount, e);
            if (failCount >= MAX_FAIL) {
                // 连续异常达到阈值，标记 workerId 无效
                workerIdValid = false;
                log.error("workerId {} 连续 {} 次续租异常，标记为无效", this.workerId, MAX_FAIL);
            }
        }
    }

    /**
     * 生成全局唯一 ID。
     *
     * <p>本方法基于 Snowflake 算法生成 64-bit long 类型唯一 ID，确保单节点内时间递增且多节点环境下避免冲突。</p>
     *
     * <h2>核心逻辑</h2>
     * <ul>
     *     <li>获取当前毫秒时间戳。</li>
     *     <li>检测时钟回拨：
     *         <ul>
     *             <li>回拨超过 5ms，直接抛出 {@link IllegalStateException}。</li>
     *             <li>回拨 ≤ 5ms，采用 wait 等待机制，尝试追上上次时间戳。</li>
     *         </ul>
     *     </li>
     *     <li>同一毫秒内：
     *         <ul>
     *             <li>序列号自增，保证不超过 12 位 (4096)。</li>
     *             <li>序列号溢出时，随机初始化 0~99，缓解热点冲突。</li>
     *         </ul>
     *     </li>
     *     <li>新毫秒开始：
     *         <ul>
     *             <li>序列号随机初始化 0~99，减轻多请求集中在序列号低位的冲突。</li>
     *         </ul>
     *     </li>
     *     <li>最后通过位移组合时间戳、节点 ID 和序列号生成最终唯一 ID。</li>
     * </ul>
     *
     * <h2>异常处理</h2>
     * <ul>
     *     <li>如果线程在 wait 过程中被中断，恢复中断状态并返回 -2。</li>
     *     <li>如果等待结束后仍未追上上次时间戳，返回 -1。</li>
     *     <li>回拨超过 5ms 时抛出异常，避免生成重复 ID。</li>
     * </ul>
     *
     * <h2>设计防护点</h2>
     * <ul>
     *     <li>序列号掩码确保溢出安全。</li>
     *     <li>随机起点缓解同毫秒热点冲突。</li>
     *     <li>时钟回拨检测避免 ID 重复。</li>
     *     <li>synchronized 确保单节点线程安全。</li>
     * </ul>
     *
     * <h2>业务场景</h2>
     * <ul>
     *     <li>分布式系统生成唯一 ID，替代数据库自增 ID 或 UUID。</li>
     *     <li>适用于高并发环境，每毫秒可生成数千个 ID。</li>
     *     <li>支持业务扩展，可通过 key 参数进行业务标识或日志追踪。</li>
     * </ul>
     *
     * @param bizTag 业务 key，可用于扩展或日志标识
     * @return 生成的唯一 ID；异常或回拨处理返回负数
     */
    @Override
    public synchronized long allocate(String bizTag) {
        if (!workerIdValid) {
            // 当前 workerId 无效时，抛异常
            throw new BusinessException(InfraErrorCode.WORKER_ID_INVALID);
        }

        // 当前毫秒时间戳
        long timestamp = clockTick();

        // 时钟回拨处理
        while (timestamp < this.lastTimestamp) {
            // 计算回拨了多少毫秒
            long offset = this.lastTimestamp - timestamp;
            // 回拨 >5ms, 直接失败
            if (offset > 5) {
                log.error("时钟回拨超过5毫秒允许范围, 最后一次申请序列时间戳:{}, 当前时间戳:{}", this.lastTimestamp, timestamp);
                throw new BusinessException(InfraErrorCode.CLOCK_BACKWARD);
            }

            try {
                /*
                 * 等待并让出锁, 等待时间 = 回拨时间 * 2
                 * 回拨 ≤5ms 时等待时间追平
                 * 使用 wait 而不是 sleep，释放锁让其他线程也可以进来检查时间
                 * 避免高并发场景下单线程持锁导致吞吐骤降
                 */
                wait(offset << 1);
                timestamp = clockTick();
                if (timestamp < this.lastTimestamp) {
                    log.error("回拨 ≤5ms, 忙等待仍回拨, 最后一次申请序列时间戳:{}, 当前时间戳:{}", this.lastTimestamp, timestamp);
                    throw new BusinessException(InfraErrorCode.CLOCK_BACKWARD);
                }
            } catch (InterruptedException e) {
                // 恢复中断状态
                Thread.currentThread().interrupt();
                log.error("回拨 ≤5ms, 线程被中断, 最后一次申请序列时间戳:{}, 当前时间戳:{}", this.lastTimestamp, timestamp);
                throw new BusinessException(InfraErrorCode.CLOCK_BACKWARD);
            }
        }

        // 同一毫秒内再次发号
        if (timestamp == this.lastTimestamp) {
            // 毫秒内序列号自增, 并用 mask 保证不溢出 12 bit
            this.sequence = (this.sequence + 1) & SEQUENCE_MASK;
            // 如果自增后变成 0, 意味着一毫秒内 4096 个号用完了(12 bit)
            if (this.sequence == 0) {
                // 缓解同一毫秒内热点冲突 (抗热点策略)
                this.sequence = RANDOM.nextInt(100);
                // 因为当前时间已经消耗完毕序列号, 忙等直到进入下一毫秒
                timestamp = tilNextMillis(this.lastTimestamp);
            }
        } else {// 新的毫秒开始, 序列号重置
            this.sequence = RANDOM.nextInt(100);
        }

        // 记录这次用到的时间戳，为下次比较做准备
        this.lastTimestamp = timestamp;

        /*
         * 核心公式：
         * timestamp - epochStart:  相对纪元的时间差    → 放高位
         * workerId:                节点的编号         → 放中位
         * sequence:                毫秒内序号         → 放低位
         * 通过位移 + 或运算，把三块信息压成一个 long
         */
        return (timestamp - EPOCH_START) << TIMESTAMP_LEFT_SHIFT
            | (workerId << WORKER_ID_SHIFT)
            | this.sequence;
    }

    /**
     * 忙等直到进入下一毫秒
     *
     * @param lastTimestamp 上次时间戳
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = clockTick();
        while (timestamp <= lastTimestamp) {
            /*
             * JDK 9+ CPU hint
             * 减少 busy-wait CPU 消耗
             */
            Thread.onSpinWait();
            timestamp = clockTick();
        }

        return timestamp;
    }

    /**
     * 获取当前毫秒时间戳
     *
     * @return 当前时间 (毫秒)
     */
    protected long clockTick() {
        return System.currentTimeMillis();
    }

    /**
     * 服务优雅关闭时释放 workerId
     * 基于这样的机制, 释放 workerId的时机 在 `Tomcat并未停止接收请求` 之前
     * 导致停服瞬间, workerId释放, 但还是有请求进入
     * 虽不影响取号问题。但是体验略差
     */
    @EventListener
    public void onContextClosed(ContextClosedEvent event) {
        if (!workerIdValid || workerId < 0) {
            return;
        }

        // 先标记为无效，阻止 allocate 再返回数据
        workerIdValid = false;

        try {
            boolean ok = redisWorkerIdManager.releaseWorkerId(
                this.workerId
            );
            if (ok) {
                log.info("释放 workerId: {} 成功", this.workerId);
            } else {
                log.warn("释放 workerId: {} 失败", this.workerId);
            }
        } catch (Exception e) {
            log.error("释放 workerId: {} 异常", this.workerId, e);
        }
    }
}
