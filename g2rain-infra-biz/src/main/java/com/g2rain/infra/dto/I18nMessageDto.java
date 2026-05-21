package com.g2rain.infra.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 国际化信息表查询DTO
 * 表名: i18n_message
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "国际化信息提交 DTO")
public class I18nMessageDto extends BaseDto {

    /**
     * 国际化用途编码
     */
    @NotBlank
    @Schema(description = "国际化用途编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String messageUsageCode;

    /**
     * 业务标签
     */
    @Schema(description = "业务标签")
    private String tag;

    /**
     * 语言编码,如 zh
     */
    @NotBlank
    @Schema(description = "语言编码,如 zh", requiredMode = Schema.RequiredMode.REQUIRED)
    private String languageCode;

    /**
     * 国家/地区编码,如 CN
     */
    @Schema(description = "国家/地区编码,如 CN")
    private String regionCode;

    /**
     * 国际化消息编码(唯一)
     */
    @NotBlank
    @Schema(description = "国际化消息编码(唯一)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String messageCode;

    /**
     * 国际化内容文本
     */
    @NotBlank
    @Schema(description = "国际化内容文本", requiredMode = Schema.RequiredMode.REQUIRED)
    private String messageText;

    /**
     * 扩展字段,存储额外格式化内容
     */
    @Schema(description = "扩展字段,存储额外格式化内容")
    private String extendField;
}
