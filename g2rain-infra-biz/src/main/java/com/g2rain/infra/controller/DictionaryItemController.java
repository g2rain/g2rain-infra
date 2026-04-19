package com.g2rain.infra.controller;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.infra.api.DictionaryItemApi;
import com.g2rain.infra.dto.DictionaryItemDto;
import com.g2rain.infra.dto.DictionaryItemSelectDto;
import com.g2rain.infra.dto.DictionaryItemTreeSelectDto;
import com.g2rain.infra.service.DictionaryItemService;
import com.g2rain.infra.vo.DictionaryItemTreeVo;
import com.g2rain.infra.vo.DictionaryItemVo;
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
 * 字典明细表控制器
 * 表名: dictionary_item
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/dictionary_item")
public class DictionaryItemController implements DictionaryItemApi {

    @Resource(name = "dictionaryItemServiceImpl")
    private DictionaryItemService dictionaryItemService;

    @Override
    public Result<List<DictionaryItemVo>> selectList(DictionaryItemSelectDto selectDto) {
        return Result.success(dictionaryItemService.selectList(selectDto));
    }

    @Override
    public Result<PageData<DictionaryItemVo>> selectPage(PageSelectListDto<DictionaryItemSelectDto> selectDto) {
        return Result.successPage(dictionaryItemService.selectPage(selectDto));
    }

    @Override
    public Result<List<DictionaryItemTreeVo>> selectTree(DictionaryItemTreeSelectDto selectDto) {
        return Result.success(dictionaryItemService.selectTree(selectDto));
    }

    /**
     * 新增或更新字典明细
     *
     * @param dto 字典明细数据传输对象
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新字典明细", description = "新增或更新字典明细信息")
    public Result<Long> save(@RequestBody DictionaryItemDto dto) {
        return Result.success(dictionaryItemService.save(dto));
    }

    /**
     * 根据主键删除字典明细
     *
     * @param id 字典明细主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除字典明细记录", description = "根据主键删除字典明细记录")
    public Result<Integer> delete(@Parameter(description = "字典明细标识") @PathVariable Long id) {
        return Result.success(dictionaryItemService.delete(id));
    }

}
