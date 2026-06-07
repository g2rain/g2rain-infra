package com.g2rain.infra.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


/**
 * 国际化信息表查询入参DTO
 * 用于I18nMessageDao.selectList方法的条件筛选
 * 表名: i18n_message
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "国际化信息查询条件 DTO")
public class I18nMessageSelectDto extends BaseSelectListDto {

    /**
     * 国际化用途编码
     */
    @Schema(description = "国际化用途编码")
    private String messageUsageCode;

    /**
     * 业务标签
     */
    @Schema(description = "业务标签")
    private String tag;

    /**
     * 业务标签集合
     */
    @Schema(description = "业务标签集合")
    private Set<String> tags;

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
     * 是否仅匹配空地区（region_code 为 NULL 或空字符串）
     */
    @Schema(hidden = true, description = "是否仅匹配空地区编码")
    private Boolean matchEmptyRegionCode;

    /**
     * 国际化消息编码(唯一)
     */
    @Schema(description = "国际化消息编码")
    private String messageCode;

    /**
     * 国际化内容文本
     */
    @Schema(description = "国际化内容文本")
    private String messageText;

    /**
     * 扩展字段,存储额外格式化内容
     */
    @Schema(description = "扩展字段,存储额外格式化内容")
    private String extendField;
}
