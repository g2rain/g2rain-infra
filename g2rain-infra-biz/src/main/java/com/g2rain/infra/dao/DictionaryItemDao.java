package com.g2rain.infra.dao;

import com.g2rain.infra.dao.po.DictionaryItemPo;
import com.g2rain.infra.dto.DictionaryItemSelectDto;
import com.g2rain.infra.dto.DictionaryLocalizedSelectDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 字典明细表数据访问接口
 * 表名: dictionary_item
 *
 * @author G2rain Generator
 */
@Mapper
public interface DictionaryItemDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(DictionaryItemPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<DictionaryItemPo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(DictionaryItemPo entity);

    /**
     * 根据 ID 删除记录
     *
     * @param id 主键 ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据ID和Version更新记录（乐观锁更新）
     *
     * @param entity 实体对象（必须包含version字段）
     * @return 影响行数
     */
    int updateByVersion(DictionaryItemPo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    DictionaryItemPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<DictionaryItemPo> selectList(DictionaryItemSelectDto selectDto);

    /**
     * 检查字典明细是否存在
     *
     * @param selectDto 查询条件
     * @return 字典明细数量
     */
    Long checkDictItemExists(DictionaryItemSelectDto selectDto);

    /**
     * 查询字典明细列表
     *
     * @param selectDto 查询条件
     * @return 实体对象列表
     */
    List<DictionaryItemPo> selectLocalizedList(DictionaryLocalizedSelectDto selectDto);
}
