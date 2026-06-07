package com.g2rain.infra.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "语言区域映射 VO")
public class LocaleCodeNameVo {

    /**
     * 语言区域编码
     */
    @Schema(description = "语言区域编码")
    private String code;

    /**
     * 语言区域名称
     */
    @Schema(description = "语言区域名称")
    private String name;
}
