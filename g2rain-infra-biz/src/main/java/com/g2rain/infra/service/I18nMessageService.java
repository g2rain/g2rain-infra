package com.g2rain.infra.service;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.infra.dto.I18nMessageDto;
import com.g2rain.infra.dto.I18nMessageSelectDto;
import com.g2rain.infra.vo.I18nMessageVo;

import java.util.List;

/**
 * 国际化信息表服务接口
 * 表名: i18n_message
 *
 * @author G2rain Generator
 */
public interface I18nMessageService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件DTO
     * @return VO对象列表
     */
    List<I18nMessageVo> selectList(I18nMessageSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页VO数据
     */
    PageData<I18nMessageVo> selectPage(PageSelectListDto<I18nMessageSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(I18nMessageDto dto);

    /**
     * 根据ID删除数据
     *
     * @param id 主键ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}