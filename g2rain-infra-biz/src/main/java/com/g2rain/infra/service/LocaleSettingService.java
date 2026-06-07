package com.g2rain.infra.service;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.infra.dto.LocaleSettingDto;
import com.g2rain.infra.dto.LocaleSettingSelectDto;
import com.g2rain.infra.vo.LocaleCodeNameVo;
import com.g2rain.infra.vo.LocaleSettingVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<LocaleSettingVo> selectList(LocaleSettingSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
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
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);

    /**
     * 获取地域-语言字典
     *
     * @return 区域字典集合
     */
    Set<String> localeDict();

    /**
     * 获取语言地域映射
     *
     * @return 语言 地域映射
     */
    Map<String, Set<String>> getLanguageCountries();

    /**
     * 获取地域语言编码和名称映射集合
     *
     * @return 地域语言编码和名称映射集合
     */
    List<LocaleCodeNameVo> code2name();
}
