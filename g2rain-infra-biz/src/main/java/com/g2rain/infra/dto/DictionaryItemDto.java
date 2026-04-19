package com.g2rain.infra.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 字典明细表查询DTO
 * 表名: dictionary_item
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典明细提交 DTO")
public class DictionaryItemDto extends BaseDto {

    /**
     * 父节点ID,用于 tree 结构字典
     */
    @Schema(description = "父节点 ID,用于 tree 结构字典")
    private Long parentId;

    /**
     * 字典用途主键标识
     */
    @Schema(description = "字典用途主键标识")
    private Long dictionaryUsageId;

    /**
     * 字典项编码,用于系统标识
     */
    @Schema(description = "字典项编码,用于系统标识")
    private String code;

    /**
     * 字典名称(默认语言)
     */
    @Schema(description = "字典名称(默认语言)")
    private String name;

    /**
     * 业务描述
     */
    @Schema(description = "业务描述")
    private String description;

    /**
     * 字典排序
     */
    @Schema(description = "字典排序")
    private Integer sortIndex;
}