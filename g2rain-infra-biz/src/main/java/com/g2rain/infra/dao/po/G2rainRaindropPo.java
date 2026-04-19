package com.g2rain.infra.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 全局唯一ID管理表返回Po
 * 关联表名: g2rain_raindrop
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class G2rainRaindropPo extends BasePo {

    /**
     * 业务标识,每个业务对应一行
     */
    private String bizTag;

    /**
     * 当前分配到的最大ID
     */
    private Long maxId;

    /**
     * 分配步长,用于批量预分配ID
     */
    private Integer step;

    /**
     * 业务描述
     */
    private String description;
}