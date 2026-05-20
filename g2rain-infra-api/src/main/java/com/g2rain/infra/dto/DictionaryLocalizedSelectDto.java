package com.g2rain.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 字典组件查询入参（DictSelect / DictText / loadByUsageCode 统一入参）
 * <p>
 * languageCode、regionCode 由服务端根据 Accept-Language 上下文注入，不作为 API 入参。
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "字典组件查询条件 DTO")
public class DictionaryLocalizedSelectDto {

    /**
     * 按名称模糊搜索（有国际化时匹配 message_text，否则匹配字典默认 name）
     */
    @Schema(description = "按名称模糊搜索")
    private String name;

    /**
     * 按 code 精确查询（DictText 回显）
     */
    @Schema(description = "按 code 精确查询")
    private String code;

    /**
     * 字典用途代码
     */
    @Schema(description = "字典用途代码")
    private String usageCode;

    /**
     * 语言编码,如 zh（服务端注入）
     */
    @Schema(hidden = true, description = "语言编码")
    private String languageCode;

    /**
     * 国家/地区编码,如 CN（服务端注入，无地区时为空）
     */
    @Schema(hidden = true, description = "国家/地区编码")
    private String regionCode;
}
