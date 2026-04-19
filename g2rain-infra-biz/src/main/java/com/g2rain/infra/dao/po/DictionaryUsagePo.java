package com.g2rain.infra.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 字典用途表返回Po
 * 关联表名: dictionary_usage
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DictionaryUsagePo extends BasePo {

    /**
     * 字典用途代码
     */
    private String usageCode;

    /**
     * 字典用途名称
     */
    private String usageName;

    /**
     * 业务描述
     */
    private String description;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}