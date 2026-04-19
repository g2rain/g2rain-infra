package com.g2rain.infra.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * KeysmithType 枚举
 * <p>
 * 表示 Keysmith 接口不同实现类型，用于在 G2rainRaindropServiceImpl
 * 中根据类型选择具体的 Keysmith 实现。
 */
@Schema(description = "Keysmith ID 生成实现类型")
public enum KeysmithType {
    /**
     * SegmentKeysmith 类型，用于基于分段策略生成 ID
     */
    @Schema(description = "分段策略生成 ID")
    SEGMENT,

    /**
     * SnowflakeKeysmith 类型，用于基于 Snowflake 算法生成分布式 ID
     */
    @Schema(description = "Snowflake 算法生成分布式 ID")
    SNOWFLAKE
}
