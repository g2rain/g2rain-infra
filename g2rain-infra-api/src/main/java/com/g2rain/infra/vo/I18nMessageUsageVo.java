package com.g2rain.infra.vo;

import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 国际化信息用途表返回VO
 * 关联表名: i18n_message_usage
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "国际化信息用途 VO")
public class I18nMessageUsageVo extends BaseVo {

    /**
     * 用途编码,用于在代码中标识用途:DICTIONARY 字典, ERROR_CODE 错误码为固定用途
     */
    @Schema(description = "用途编码,如 DICTIONARY、ERROR_CODE 等固定用途")
    private String usageCode;

    /**
     * 用途名称
     */
    @Schema(description = "用途名称")
    private String name;

    /**
     * 业务描述
     */
    @Schema(description = "业务描述")
    private String remark;
}
