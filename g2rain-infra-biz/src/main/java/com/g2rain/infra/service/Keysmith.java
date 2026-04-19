package com.g2rain.infra.service;


import com.g2rain.infra.enums.KeysmithType;
import com.g2rain.infra.service.impl.SegmentKeysmith;
import com.g2rain.infra.service.impl.SnowflakeKeysmith;


/**
 * 接口 Keysmith 定义了生成唯一 ID 的策略。
 * <p>
 * 每个实现类对应一个 {@link KeysmithType} 枚举值，可在
 * {@code G2rainRaindropServiceImpl} 中根据类型选择具体实现。
 * </p>
 * <p>
 * 典型实现：
 * <ul>
 *   <li>{@link SegmentKeysmith} - 基于分段策略生成 ID</li>
 *   <li>{@link SnowflakeKeysmith} - 基于 Snowflake 算法生成分布式 ID</li>
 * </ul>
 * </p>
 * <p>业务场景：在分布式系统中为资源生成唯一标识。</p>
 *
 * @author alpha
 * @since 2025-12-25
 */
public interface Keysmith {
    /**
     * 获取当前实现类对应的 Keysmith 类型。
     *
     * @return {@link KeysmithType} 枚举值
     */
    KeysmithType type();

    /**
     * 为指定业务 key 分配或生成唯一 ID。
     * <p>
     * 不同实现类根据各自策略生成 ID。例如：
     * <ul>
     *   <li>SegmentKeysmith 按分段策略生成</li>
     *   <li>SnowflakeKeysmith 按 Snowflake 算法生成</li>
     * </ul>
     * </p>
     *
     * @param bizTag 业务 key，用于生成 ID
     * @return 唯一 ID 值
     */
    long allocate(String bizTag);
}
