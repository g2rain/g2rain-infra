package com.g2rain.infra.controller;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.infra.api.I18nMessageUsageApi;
import com.g2rain.infra.dto.I18nMessageUsageDto;
import com.g2rain.infra.dto.I18nMessageUsageSelectDto;
import com.g2rain.infra.service.I18nMessageUsageService;
import com.g2rain.infra.vo.I18nMessageUsageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 国际化信息用途表控制器
 * 表名: i18n_message_usage
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/i18n_message_usage")
public class I18nMessageUsageController implements I18nMessageUsageApi {

    @Resource(name = "i18nMessageUsageServiceImpl")
    private I18nMessageUsageService i18nMessageUsageService;

    @Override
    public Result<List<I18nMessageUsageVo>> selectList(I18nMessageUsageSelectDto selectDto) {
        return Result.success(i18nMessageUsageService.selectList(selectDto));
    }

    @Override
    public Result<PageData<I18nMessageUsageVo>> selectPage(PageSelectListDto<I18nMessageUsageSelectDto> selectDto) {
        return Result.successPage(i18nMessageUsageService.selectPage(selectDto));
    }

    /**
     * 新增或更新国际化信息用途
     *
     * @param dto 国际化信息用途数据传输对象
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新国际化信息用途", description = "新增或更新国际化信息用途关联")
    public Result<Long> save(@RequestBody I18nMessageUsageDto dto) {
        return Result.success(i18nMessageUsageService.save(dto));
    }

    /**
     * 根据主键删除国际化信息用途
     *
     * @param id 国际化信息用途主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除国际化信息用途记录", description = "根据主键删除国际化信息用途记录")
    public Result<Integer> delete(@Parameter(description = "国际化信息用途标识") @PathVariable Long id) {
        return Result.success(i18nMessageUsageService.delete(id));
    }
}
