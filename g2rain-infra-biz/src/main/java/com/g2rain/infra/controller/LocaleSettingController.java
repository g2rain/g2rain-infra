package com.g2rain.infra.controller;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.infra.api.LocaleSettingApi;
import com.g2rain.infra.dto.LocaleSettingDto;
import com.g2rain.infra.dto.LocaleSettingSelectDto;
import com.g2rain.infra.service.LocaleSettingService;
import com.g2rain.infra.vo.LocaleCodeNameVo;
import com.g2rain.infra.vo.LocaleSettingVo;
import com.g2rain.web.interceptors.annotations.LoginGuard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 地域-语言设置表控制器
 * 表名: locale_setting
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/locale_setting")
public class LocaleSettingController implements LocaleSettingApi {

    @Resource(name = "localeSettingServiceImpl")
    private LocaleSettingService localeSettingService;

    @Override
    public Result<List<LocaleSettingVo>> selectList(LocaleSettingSelectDto selectDto) {
        return Result.success(localeSettingService.selectList(selectDto));
    }

    @Override
    public Result<PageData<LocaleSettingVo>> selectPage(PageSelectListDto<LocaleSettingSelectDto> selectDto) {
        return Result.successPage(localeSettingService.selectPage(selectDto));
    }

    /**
     * 新增或更新地域-语言设置
     *
     * @param dto 地域语言设置数据传输对象
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新地域语言设置", description = "新增或更新地域与语言偏好配置")
    public Result<Long> save(@RequestBody @Validated LocaleSettingDto dto) {
        return Result.success(localeSettingService.save(dto));
    }

    /**
     * 根据主键删除地域-语言设置
     *
     * @param id 地域语言设置主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除地域语言设置记录", description = "根据主键删除地域-语言设置记录")
    public Result<Integer> delete(@Parameter(description = "地域语言设置标识") @PathVariable Long id) {
        return Result.success(localeSettingService.delete(id));
    }

    /**
     * 获取地域语言字典
     *
     * @return 受影响的记录行数
     */
    @GetMapping("/locale_dict")
    @Operation(summary = "获取地域语言字典", description = "获取地域语言字典")
    public Result<Set<String>> localeDict() {
        return Result.success(localeSettingService.localeDict());
    }

    /**
     * 获取语言地域映射
     *
     * @return 受影响的记录行数
     */
    @GetMapping("/get_language_countries")
    @Operation(summary = "获取语言地域映射", description = "获取语言地域映射")
    public Result<Map<String, Set<String>>> getLanguageCountries() {
        return Result.success(localeSettingService.getLanguageCountries());
    }

    /**
     * 获取地域语言编码和名称映射集合
     *
     * @return 受影响的记录行数
     */
    @LoginGuard(require = false)
    @GetMapping("/code_name_map")
    @Operation(summary = "获取地域语言编码和名称映射集合", description = "获取地域语言编码和名称映射集合")
    public Result<List<LocaleCodeNameVo>> code2name() {
        return Result.success(localeSettingService.code2name());
    }
}
