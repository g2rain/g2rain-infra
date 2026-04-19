package com.g2rain.infra.vo;

import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 全局唯一ID管理表返回VO
 * 关联表名: g2rain_raindrop
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "全局唯一 ID 管理 VO")
public class G2rainRaindropVo extends BaseVo {

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
