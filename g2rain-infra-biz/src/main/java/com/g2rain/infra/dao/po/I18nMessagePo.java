package com.g2rain.infra.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 国际化信息表返回Po
 * 关联表名: i18n_message
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class I18nMessagePo extends BasePo {

    /**
     * 用途标识
     */
    private Long messageUsageId;

    /**
     * 语言编码,如 zh
     */
    private String languageCode;

    /**
     * 国家/地区编码,如 CN
     */
    private String regionCode;

    /**
     * 国际化消息编码(唯一)
     */
    private String messageCode;

    /**
     * 国际化内容文本
     */
    private String messageText;

    /**
     * 扩展字段,存储额外格式化内容
     */
    private String extendField;
}