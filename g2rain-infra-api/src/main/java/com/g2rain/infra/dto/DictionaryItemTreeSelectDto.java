package com.g2rain.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 查询树形字典入参DTO
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

    @NotNull
    @Schema(description = "字典用途ID")
    private Long dictionaryUsageId;
}
