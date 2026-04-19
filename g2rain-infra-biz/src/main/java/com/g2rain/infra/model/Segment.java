package com.g2rain.infra.model;


import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <h1>Segment</h1>
 *
 * <p>分布式 ID 分配器中的号段对象，用于维护连续的 ID 区间。</p>
 *
 * <h2>核心设计</h2>
 * <ul>
 *     <li>使用 AtomicLong 保证多线程环境下 ID 自增的原子性。</li>
 *     <li>step 表示该号段的步长（ID 个数），可根据实际使用动态调整。</li>
 *     <li>max 表示该号段可分配的最大 ID，用于判断号段是否耗尽。</li>
 *     <li>通过 getIdle() 获取剩余 ID 数量，用于触发下一个 Segment 异步预加载。</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <ul>
 *     <li>与 {@link SegmentBuffer} 配合，实现双缓冲号段策略。</li>
 *     <li>在高并发分布式系统中，保证每个节点 ID 连续性及线程安全。</li>
 * </ul>
 *
 * <h2>示例</h2>
 * <pre>{@code
 * SegmentBuffer buffer = new SegmentBuffer();
 * Segment segment = new Segment(buffer);
 * segment.setStep(1000);
 * segment.setMax(1000);
 * long id = segment.getValue().getAndIncrement();
 * long idle = segment.getIdle();
 * System.out.println("ID=" + id + ", 剩余=" + idle);
 * }</pre>
 *
 * <p>注意：该类不负责持久化，实际 max 和 step 需由数据库或 SegmentBuffer 管理。</p>
 *
 * @author alpha
 * @since 2025/12/25
 */
@Getter
public class Segment {

    /**
     * <p>所属的 SegmentBuffer 上下文，表示该 Segment 的双缓冲管理单元。</p>
     *
     * <p>通过 buffer 可以访问下一个 Segment 或其他缓存状态信息。</p>
     */
    private final SegmentBuffer buffer;

    /**
     * <p>当前可分配 ID 值</p>
     *
     * <ul>
     *     <li>使用 AtomicLong 保证并发环境下自增操作原子性</li>
     *     <li>初始值为 0</li>
     * </ul>
     */
    private final AtomicLong value;

    /**
     * <p>当前号段的步长，即 Segment 包含的 ID 个数</p>
     *
     * <ul>
     *     <li>步长可能动态调整，防止频繁访问数据库</li>
     *     <li>例如：首次分配 1000，15 分钟内增长或减半</li>
     * </ul>
     */
    @Setter
    private volatile int step;

    /**
     * <p>当前号段的最大可分配 ID</p>
     *
     * <ul>
     *     <li>取号范围为 [value, max)</li>
     *     <li>当 value 达到 max 时，该号段耗尽，需要切换下一个 Segment</li>
     * </ul>
     */
    @Setter
    private volatile long max;

    /**
     * <p>构造方法</p>
     *
     * @param buffer 所属的 SegmentBuffer 上下文
     */
    public Segment(SegmentBuffer buffer) {
        this.buffer = buffer;
        // 初始 ID 为 0
        this.value = new AtomicLong(0);
    }

    /**
     * <p>获取当前号段剩余可分配 ID 数量</p>
     *
     * <p>剩余 = max - 当前 value</p>
     *
     * <ul>
     *     <li>用于判断是否触发下一个 Segment 的异步预加载</li>
     *     <li>多线程安全，value 自增后可立即反映剩余数量</li>
     * </ul>
     *
     * @return 当前号段剩余可分配 ID
     */
    public long getIdle() {
        return this.getMax() - getValue().get();
    }

    @Override
    public String toString() {
        return String.format("{\"value\": %d, \"step\": %d, \"max\": %d, \"idle\": %d}",
            value.get(),
            step,
            max,
            getIdle()
        );
    }
}
