package com.g2rain.infra.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 地域-语言设置表返回Po
 * 关联表名: locale_setting
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LocaleSettingPo extends BasePo {

    /**
     * 语言编码,如 zh
     */
    private String languageCode;

    /**
     * 国家/地区编码,如 CN
     */
    private String regionCode;

    /**
     * 区域标识,如 zh-CN
     */
    private String code;

    /**
     * 区域名称,如 中国[简体中文]
     */
    private String name;

    /**
     * 语言描述
     */
    private String description;
}