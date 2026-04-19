package com.g2rain.infra.vo;

import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 国际化信息表返回VO
 * 关联表名: i18n_message
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "国际化信息 VO")
public class I18nMessageVo extends BaseVo {

    /**
     * 用途标识
     */
    @Schema(description = "国际化信息用途标识")
    private Long messageUsageId;

    /**
     * 用途名称
     */
    @Schema(description = "国际化信息用途名称")
    private String messageUsageName;

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
