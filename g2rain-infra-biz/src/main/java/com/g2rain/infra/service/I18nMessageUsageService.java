package com.g2rain.infra.service;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.infra.dto.I18nMessageUsageDto;
import com.g2rain.infra.dto.I18nMessageUsageSelectDto;
import com.g2rain.infra.vo.I18nMessageUsageVo;

import java.util.List;

/**
 * 国际化信息用途表服务接口
 * 表名: i18n_message_usage
 *
 * @author G2rain Generator
 */
public interface I18nMessageUsageService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件DTO
     * @return VO对象列表
     */
    List<I18nMessageUsageVo> selectList(I18nMessageUsageSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页VO数据
     */
    PageData<I18nMessageUsageVo> selectPage(PageSelectListDto<I18nMessageUsageSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(I18nMessageUsageDto dto);

    /**
     * 根据ID删除数据
     *
     * @param id 主键ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}