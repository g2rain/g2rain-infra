package com.g2rain.infra.api;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.infra.dto.DictionaryUsageSelectDto;
import com.g2rain.infra.vo.DictionaryUsageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 字典用途表API接口
 * 表名: dictionary_usage
 *
 * @author G2rain Generator
 */
@Tag(name = "字典用途", description = "字典用途表相关接口")
public interface DictionaryUsageApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询字典用途列表", description = "根据查询条件返回字典用途列表")
    Result<List<DictionaryUsageVo>> selectList(DictionaryUsageSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询字典用途列表", description = "分页查询字典用途列表")
    Result<PageData<DictionaryUsageVo>> selectPage(PageSelectListDto<DictionaryUsageSelectDto> selectDto);
}
