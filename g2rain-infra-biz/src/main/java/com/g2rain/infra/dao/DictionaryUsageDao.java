package com.g2rain.infra.dao;

import com.g2rain.infra.dao.po.DictionaryUsagePo;
import com.g2rain.infra.dto.DictionaryUsageSelectDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 字典用途表数据访问接口
 * 表名: dictionary_usage
 *
 * @author G2rain Generator
 */
@Mapper
public interface DictionaryUsageDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(DictionaryUsagePo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<DictionaryUsagePo> list);

    /**
     * 根据ID更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(DictionaryUsagePo entity);

    /**
     * 根据ID删除记录
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据ID和Version更新记录（乐观锁更新）
     *
     * @param entity 实体对象（必须包含version字段）
     * @return 影响行数
     */
    int updateByVersion(DictionaryUsagePo entity);

    /**
     * 根据ID查询记录
     *
     * @param id 主键ID
     * @return 实体对象
     */
    DictionaryUsagePo selectById(Long id);

    /**
     * 根据查询入参DTO筛选列表
     *
     * @param selectDto 查询条件DTO
     * @return 实体对象列表
     */
    List<DictionaryUsagePo> selectList(DictionaryUsageSelectDto selectDto);
}