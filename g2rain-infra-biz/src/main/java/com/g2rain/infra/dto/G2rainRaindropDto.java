package com.g2rain.infra.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 全局唯一ID管理表查询DTO
 * 表名: g2rain_raindrop
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "全局唯一 ID 管理提交 DTO")
public class G2rainRaindropDto extends BaseDto {

    /**
     * 业务标识,每个业务对应一行
     */
    @Schema(description = "业务标识,每个业务对应一行")
    private String bizTag;

    /**
     * 当前分配到的最大 ID
     */
    @Schema(description = "当前分配到的最大 ID")
    private Long maxId;

    /**
     * 分配步长,用于批量预分配ID
     */
    @Schema(description = "分配步长,用于批量预分配 ID")
    private Integer step;

    /**
     * 业务描述
     */
    @Schema(description = "业务描述")
    private String description;
}
