package com.g2rain.infra.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 字典用途表查询DTO
 * 表名: dictionary_usage
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典用途提交 DTO")
public class DictionaryUsageDto extends BaseDto {

    /**
     * 字典用途代码
     */
    @NotBlank
    @Schema(description = "字典用途代码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String usageCode;

    /**
     * 字典用途名称
     */
    @NotBlank
    @Schema(description = "字典用途名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String usageName;

    /**
     * 业务描述
     */
    @Schema(description = "业务描述")
    private String description;
}
