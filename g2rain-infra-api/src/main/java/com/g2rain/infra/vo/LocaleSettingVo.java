package com.g2rain.infra.vo;

import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 地域-语言设置表返回VO
 * 关联表名: locale_setting
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "地域语言设置 VO")
public class LocaleSettingVo extends BaseVo {

    /**
     * 语言编码,如 zh
     */
    @Schema(description = "语言编码,如 zh")
    private String languageCode;

    /**
     * 国家/地区编码,如 CN
     */
    @Schema(description = "国家/地区编码,如 CN")
    private String regionCode;

    /**
     * 区域标识,如 zh-CN
     */
    @Schema(description = "区域标识,如 zh-CN")
    private String code;

    /**
     * 区域名称,如 中国[简体中文]
     */
    @Schema(description = "区域名称,如 中国[简体中文]")
    private String name;

    /**
     * 语言描述
     */
    @Schema(description = "语言描述")
    private String description;
}
