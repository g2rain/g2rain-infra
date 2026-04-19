package com.g2rain.infra.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 字典用途表查询入参DTO
 * 用于DictionaryUsageDao.selectList方法的条件筛选
 * 表名: dictionary_usage
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典用途查询条件 DTO")
public class DictionaryUsageSelectDto extends BaseSelectListDto {

    /**
     * 字典用途代码
     */
    @Schema(description = "字典用途代码")
    private String usageCode;

    /**
     * 字典用途名称
     */
    @Schema(description = "字典用途名称")
    private String usageName;

    /**
     * 业务描述
     */
    @Schema(description = "业务描述")
    private String description;
}
