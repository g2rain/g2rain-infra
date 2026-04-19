package com.g2rain.infra.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
     * 用途标识
     */
    @Schema(description = "国际化信息用途标识")
    private Long messageUsageId;

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
     * 国际化消息编码(唯一)
     */
    @Schema(description = "国际化消息编码(唯一)")
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
