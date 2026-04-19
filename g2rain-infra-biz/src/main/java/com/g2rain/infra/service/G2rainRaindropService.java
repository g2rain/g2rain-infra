package com.g2rain.infra.service;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.infra.dto.G2rainRaindropDto;
import com.g2rain.infra.dto.G2rainRaindropSelectDto;
import com.g2rain.infra.enums.KeysmithType;
import com.g2rain.infra.vo.G2rainRaindropVo;

import java.util.List;

/**
 * 全局唯一ID管理表服务接口
 * 表名: g2rain_raindrop
 *
 * @author G2rain Generator
 */
public interface G2rainRaindropService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件DTO
     * @return VO对象列表
     */
    List<G2rainRaindropVo> selectList(G2rainRaindropSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页VO数据
     */
    PageData<G2rainRaindropVo> selectPage(PageSelectListDto<G2rainRaindropSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(G2rainRaindropDto dto);

    /**
     * 根据ID删除数据
     *
     * @param id 主键ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);

    /**
     * 根据业务标签分配序列
     *
     * @param type   取号策略
     * @param bizTag 业务标签
     * @return 全局唯一序列
     */
    Long allocate(KeysmithType type, String bizTag);
}
