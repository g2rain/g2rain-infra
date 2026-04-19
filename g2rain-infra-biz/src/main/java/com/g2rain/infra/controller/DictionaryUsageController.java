package com.g2rain.infra.controller;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.infra.api.DictionaryUsageApi;
import com.g2rain.infra.dto.DictionaryUsageDto;
import com.g2rain.infra.dto.DictionaryUsageSelectDto;
import com.g2rain.infra.service.DictionaryUsageService;
import com.g2rain.infra.vo.DictionaryUsageVo;
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
 * 字典用途表控制器
 * 表名: dictionary_usage
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/dictionary_usage")
public class DictionaryUsageController implements DictionaryUsageApi {

    @Resource(name = "dictionaryUsageServiceImpl")
    private DictionaryUsageService dictionaryUsageService;

    @Override
    public Result<List<DictionaryUsageVo>> selectList(DictionaryUsageSelectDto selectDto) {
        return Result.success(dictionaryUsageService.selectList(selectDto));
    }

    @Override
    public Result<PageData<DictionaryUsageVo>> selectPage(PageSelectListDto<DictionaryUsageSelectDto> selectDto) {
        return Result.successPage(dictionaryUsageService.selectPage(selectDto));
    }

    /**
     * 新增或更新字典用途
     *
     * @param dto 字典用途数据传输对象
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新字典用途", description = "新增或更新字典用途信息")
    public Result<Long> save(@RequestBody DictionaryUsageDto dto) {
        return Result.success(dictionaryUsageService.save(dto));
    }

    /**
     * 根据主键删除字典用途
     *
     * @param id 字典用途主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除字典用途记录", description = "根据主键删除字典用途记录")
    public Result<Integer> delete(@Parameter(description = "字典用途标识") @PathVariable Long id) {
        return Result.success(dictionaryUsageService.delete(id));
    }
}
