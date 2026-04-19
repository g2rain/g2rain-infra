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
 * 字典用途表返回VO
 * 关联表名: dictionary_usage
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典用途 VO")
public class DictionaryUsageVo extends BaseVo {

    /**
     * 字典用途代码
     */
    @Schema(description = "字典用途代码")
    private String usageCode;

    /**
     * 字典用途名称
     */
    @Schema(description = "字典用途名称")
    private String usageName;

    /**
     * 业务描述
     */
    @Schema(description = "业务描述")
    private String description;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    private Boolean deleteFlag;
}
