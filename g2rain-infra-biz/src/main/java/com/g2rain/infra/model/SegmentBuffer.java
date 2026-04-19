package com.g2rain.infra.model;


import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <h1>SegmentBuffer</h1>
 *
 * <p>管理某个业务 tag 的双缓冲号段，用于分布式 ID 分配器。</p>
 *
 * <p><b>核心功能</b>：
 * <ul>
 *     <li>维护当前 Segment 和下一个 Segment，实现双缓冲机制。</li>
 *     <li>支持异步预加载下一个 Segment，保证 ID 连续性和高并发性能。</li>
 *     <li>通过读写锁和 AtomicBoolean 保证多线程安全。</li>
 *     <li>动态调整步长 (step) 以平衡数据库访问频率和缓存利用率。</li>
 * </ul>
 * </p>
 *
 * <p><b>异步预加载策略</b>：
 * 当当前号段使用量超过 10% 时（即 segment.getIdle() &lt; 0.9 * segment.getStep()），
 * 后台线程会尝试刷新下一个 Segment。CAS 判断 {@link #threadRunning} 避免重复刷新，
 * 刷新成功后设置 {@link #nextReady} = true，保证主线程能够无阻塞地切换到下一个 Segment。
 * </p>
 *
 * <p><b>使用示例</b>：
 * <pre>{@code
 * SegmentBuffer buffer = new SegmentBuffer();
 * Segment current = buffer.getCurrent();
 * if (!buffer.isNextReady() && current.getIdle() < 0.9 * current.getStep()
 *     && buffer.getThreadRunning().compareAndSet(false, true)) {
 *     service.execute(() -> prefetchNextSegment(buffer, current));
 * }
 * }</pre>
 * </p>
 *
 * @author alpha
 * @since 2025/12/25
 */
public class SegmentBuffer {

    /**
     * 业务 tag，对应某个业务的 ID 序列
     */
    @Setter
    @Getter
    private String tag;

    /**
     * 双缓冲的 Segment 数组，segments[0] 和 segments[1] 轮流使用
     */
    @Getter
    private final Segment[] segments;

    /**
     * 当前正在使用的 Segment 索引
     */
    @Getter
    private volatile int currentPos;

    /**
     * 下一个 Segment 是否已准备好切换
     */
    @Setter
    @Getter
    private volatile boolean nextReady;

    /**
     * 缓存是否初始化完成
     */
    @Setter
    @Getter
    private volatile boolean initOk;

    /**
     * 标识是否已有线程正在执行 Segment 异步预加载
     */
    @Getter
    private final AtomicBoolean threadRunning;

    /**
     * 读写锁，保证多线程下对缓冲状态的安全访问
     */
    private final ReadWriteLock lock;

    /**
     * 当前步长，用于计算号段范围
     */
    @Setter
    @Getter
    private volatile int step;

    /**
     * 最小步长，防止动态调整步长过小
     */
    @Setter
    @Getter
    private volatile int minStep;

    /**
     * 上次号段更新时间，用于动态调整步长
     */
    @Getter
    private volatile long updateTimestamp;

    /**
     * 设置更新时间为当前时间，前提是缓存已经初始化完成
     */
    public void setUpdateTimestamp() {
        if (!this.initOk) {
            return;
        }

        this.updateTimestamp = System.currentTimeMillis();
    }

    /**
     * 构造函数
     * 初始化双缓冲 Segment 和相关状态
     */
    public SegmentBuffer() {
        this.segments = new Segment[]{
            new Segment(this),
            new Segment(this)
        };

        this.currentPos = 0;
        this.nextReady = false;
        this.initOk = false;
        this.threadRunning = new AtomicBoolean(false);
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * 获取当前使用的 Segment
     *
     * @return 当前 Segment 对象
     */
    public Segment getCurrent() {
        return this.segments[this.currentPos];
    }

    /**
     * 获取下一个 Segment 的索引（轮询 0/1）
     *
     * @return 下一个 Segment 的索引
     */
    public int nextPos() {
        return (this.currentPos + 1) % 2;
    }

    /**
     * 切换到下一个 Segment，更新 currentPos
     */
    public void switchPos() {
        this.currentPos = nextPos();
    }

    /**
     * 获取读锁，用于保护对 SegmentBuffer 状态的并发读取
     *
     * @return 读锁对象
     */
    public Lock rLock() {
        return this.lock.readLock();
    }

    /**
     * 获取写锁，用于保护对 SegmentBuffer 状态的修改
     *
     * @return 写锁对象
     */
    public Lock wLock() {
        return this.lock.writeLock();
    }

    @Override
    public String toString() {
        return String.format(
            "{\"tag\":\"%s\", \"current_pos\":%d, \"nextReady\":%b, \"initOk\":%b, " +
                "\"threadRunning\":%b, \"step\":%d, \"minStep\":%d, \"updateTimestamp\":%d, " +
                "\"segments\":[%s, %s]}",
            tag,
            currentPos,
            nextReady,
            initOk,
            threadRunning.get(),
            step,
            minStep,
            updateTimestamp,
            segments[0] != null ? segments[0].toString() : "null",
            segments[1] != null ? segments[1].toString() : "null"
        );
    }
}
