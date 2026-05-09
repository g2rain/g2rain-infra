package com.g2rain.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 查询树形字典入参 DTO
 *
 * @author jagger
 * @since 2026/4/13-08:45
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode()
@Schema(description = "树形字典查询条件 DTO")
public class DictionaryItemTreeSelectDto {

    /**
     * 字典用途编码
     */
    @NotBlank
    @Schema(description = "字典用途编码")
    private String usageCode;

}
