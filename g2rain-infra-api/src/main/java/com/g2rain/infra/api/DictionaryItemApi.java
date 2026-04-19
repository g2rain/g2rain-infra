package com.g2rain.infra.api;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.infra.dto.DictionaryItemSelectDto;
import com.g2rain.infra.dto.DictionaryItemTreeSelectDto;
import com.g2rain.infra.vo.DictionaryItemTreeVo;
import com.g2rain.infra.vo.DictionaryItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 字典明细表API接口
 * 表名: dictionary_item
 *
 * @author G2rain Generator
 */
@Tag(name = "字典明细", description = "字典明细表相关接口")
public interface DictionaryItemApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询字典明细列表", description = "根据查询条件返回字典明细列表")
    Result<List<DictionaryItemVo>> selectList(DictionaryItemSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询字典明细列表", description = "分页查询字典明细列表")
    Result<PageData<DictionaryItemVo>> selectPage(PageSelectListDto<DictionaryItemSelectDto> selectDto);

     /**
     * 查询树形字典
     *
     * @param selectDto 查询条件DTO
     * @return 字典树形结构
     */
    @GetMapping("/tree")
    @Operation(summary = "分页查询字典明细列表", description = "分页查询字典明细列表")
    Result<List<DictionaryItemTreeVo>> selectTree(DictionaryItemTreeSelectDto selectDto);
}
