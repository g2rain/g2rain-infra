package com.g2rain.infra.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 地域-语言设置表查询DTO
 * 表名: locale_setting
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "地域语言设置提交 DTO")
public class LocaleSettingDto extends BaseDto {
    /**
     * 区域标识,如 zh-CN
     */
    @NotBlank
    @Schema(description = "区域标识,如 zh-CN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    /**
     * 区域名称,如 中国[简体中文]
     */
    @NotBlank
    @Schema(description = "区域名称,如 中国[简体中文]", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 语言描述
     */
    @Schema(description = "语言描述")
    private String description;
}
