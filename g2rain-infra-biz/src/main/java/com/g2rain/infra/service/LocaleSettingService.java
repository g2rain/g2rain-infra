package com.g2rain.infra.service;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.infra.dto.LocaleSettingDto;
import com.g2rain.infra.dto.LocaleSettingSelectDto;
import com.g2rain.infra.vo.LocaleSettingVo;

import java.util.List;

/**
 * 地域-语言设置表服务接口
 * 表名: locale_setting
 *
 * @author G2rain Generator
 */
public interface LocaleSettingService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件DTO
     * @return VO对象列表
     */
    List<LocaleSettingVo> selectList(LocaleSettingSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页VO数据
     */
    PageData<LocaleSettingVo> selectPage(PageSelectListDto<LocaleSettingSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(LocaleSettingDto dto);

    /**
     * 根据ID删除数据
     *
     * @param id 主键ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}