package com.g2rain.infra.service;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.infra.dto.DictionaryItemDto;
import com.g2rain.infra.dto.DictionaryItemSelectDto;
import com.g2rain.infra.dto.DictionaryItemTreeSelectDto;
import com.g2rain.infra.vo.DictionaryItemTreeVo;
import com.g2rain.infra.vo.DictionaryItemVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 字典明细表服务接口
 * 表名: dictionary_item
 *
 * @author G2rain Generator
 */
public interface DictionaryItemService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件DTO
     * @return VO对象列表
     */
    List<DictionaryItemVo> selectList(DictionaryItemSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页VO数据
     */
    PageData<DictionaryItemVo> selectPage(PageSelectListDto<DictionaryItemSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(DictionaryItemDto dto);

    /**
     * 根据ID删除数据
     *
     * @param id 主键ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);

    /**
     * 查询树形字典
     *
     * @param selectDto 查询条件DTO
     * @return 字典树形结构
     */
    List<DictionaryItemTreeVo> selectTree(DictionaryItemTreeSelectDto selectDto);
}
