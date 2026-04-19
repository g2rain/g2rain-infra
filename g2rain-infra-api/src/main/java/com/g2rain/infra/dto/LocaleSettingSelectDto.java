package com.g2rain.infra.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 地域-语言设置表查询入参DTO
 * 用于LocaleSettingDao.selectList方法的条件筛选
 * 表名: locale_setting
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "地域语言设置查询条件 DTO")
public class LocaleSettingSelectDto extends BaseSelectListDto {

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
