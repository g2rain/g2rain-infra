package com.g2rain.infra.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 字典明细表返回Po
 * 关联表名: dictionary_item
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DictionaryItemPo extends BasePo {

    /**
     * 父节点ID,用于 tree 结构字典
     */
    private Long parentId;

    /**
     * 字典用途主键标识
     */
    private Long dictionaryUsageId;

    /**
     * 字典项编码,用于系统标识
     */
    private String code;

    /**
     * 字典名称(默认语言)
     */
    private String name;

    /**
     * 业务描述
     */
    private String description;

    /**
     * 字典排序
     */
    private Integer sortIndex;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}