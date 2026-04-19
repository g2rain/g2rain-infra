package com.g2rain.infra.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 国际化信息用途表查询入参DTO
 * 用于I18nMessageUsageDao.selectList方法的条件筛选
 * 表名: i18n_message_usage
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "国际化信息用途查询条件 DTO")
public class I18nMessageUsageSelectDto extends BaseSelectListDto {

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
