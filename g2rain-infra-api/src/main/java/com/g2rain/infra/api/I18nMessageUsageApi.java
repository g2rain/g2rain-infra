package com.g2rain.infra.api;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.infra.dto.I18nMessageUsageSelectDto;
import com.g2rain.infra.vo.I18nMessageUsageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 国际化信息用途表API接口
 * 表名: i18n_message_usage
 *
 * @author G2rain Generator
 */
@Tag(name = "国际化信息用途", description = "国际化信息用途表相关接口")
public interface I18nMessageUsageApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询国际化信息用途列表", description = "根据查询条件返回国际化信息用途列表")
    Result<List<I18nMessageUsageVo>> selectList(I18nMessageUsageSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询国际化信息用途列表", description = "分页查询国际化信息用途列表")
    Result<PageData<I18nMessageUsageVo>> selectPage(PageSelectListDto<I18nMessageUsageSelectDto> selectDto);
}
