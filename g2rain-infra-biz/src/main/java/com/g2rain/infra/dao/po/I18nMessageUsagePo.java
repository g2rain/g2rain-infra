package com.g2rain.infra.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 国际化信息用途表返回Po
 * 关联表名: i18n_message_usage
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class I18nMessageUsagePo extends BasePo {

    /**
     * 用途编码,用于在代码中标识用途:DICTIONARY 字典, ERROR_CODE 错误码为固定用途
     */
    private String usageCode;

    /**
     * 用途名称
     */
    private String name;

    /**
     * 业务描述
     */
    private String remark;
}