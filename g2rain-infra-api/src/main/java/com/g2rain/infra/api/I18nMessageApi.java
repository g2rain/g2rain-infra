package com.g2rain.infra.api;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.infra.dto.I18nMessageSelectDto;
import com.g2rain.infra.vo.I18nMessageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 国际化信息表API接口
 * 表名: i18n_message
 *
 * @author G2rain Generator
 */
@Tag(name = "国际化信息", description = "国际化信息表相关接口")
public interface I18nMessageApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询国际化信息列表", description = "根据查询条件返回国际化信息列表")
    Result<List<I18nMessageVo>> selectList(I18nMessageSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询国际化信息列表", description = "分页查询国际化信息列表")
    Result<PageData<I18nMessageVo>> selectPage(PageSelectListDto<I18nMessageSelectDto> selectDto);
}
