package com.g2rain.infra.api;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.infra.dto.LocaleSettingSelectDto;
import com.g2rain.infra.vo.LocaleSettingVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 地域-语言设置表API接口
 * 表名: locale_setting
 *
 * @author G2rain Generator
 */
@Tag(name = "地域语言", description = "地域-语言设置表相关接口")
public interface LocaleSettingApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询地域语言设置列表", description = "根据查询条件返回地域-语言设置列表")
    Result<List<LocaleSettingVo>> selectList(LocaleSettingSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询地域语言设置列表", description = "分页查询地域-语言设置列表")
    Result<PageData<LocaleSettingVo>> selectPage(PageSelectListDto<LocaleSettingSelectDto> selectDto);
}
