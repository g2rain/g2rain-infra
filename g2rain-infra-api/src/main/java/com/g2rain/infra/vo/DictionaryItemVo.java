package com.g2rain.infra.vo;

import com.g2rain.common.json.AdminCompanyCondition;
import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 字典明细表返回VO
 * 关联表名: dictionary_item
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典明细 VO")
public class DictionaryItemVo extends BaseVo {

    /**
     * 父节点ID,用于 tree 结构字典
     */
    @Schema(description = "父节点 ID,用于 tree 结构字典")
    private Long parentId;

    /**
     * 父节点名称
     */
    @Schema(description = "父节点名称")
    private String parentName;

    /**
     * 字典用途主键标识
     */
    @Schema(description = "字典用途主键标识")
    private Long dictionaryUsageId;

    /**
     * 字典项编码,用于系统标识
     */
    @Schema(description = "字典项编码,用于系统标识")
    private String code;

    /**
     * 字典名称(默认语言)
     */
    @Schema(description = "字典名称(默认语言)")
    private String name;

    /**
     * 业务描述
     */
    @Schema(description = "业务描述")
    private String description;

    /**
     * 字典排序
     */
    @Schema(description = "字典排序")
    private Integer sortIndex;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    private Boolean deleteFlag;
}
