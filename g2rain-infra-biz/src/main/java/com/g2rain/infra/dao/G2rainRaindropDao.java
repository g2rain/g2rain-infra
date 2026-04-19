package com.g2rain.infra.dao;

import com.g2rain.infra.dao.po.G2rainRaindropPo;
import com.g2rain.infra.dto.G2rainRaindropSelectDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 全局唯一ID管理表数据访问接口
 * 表名: g2rain_raindrop
 *
 * @author G2rain Generator
 */
@Mapper
public interface G2rainRaindropDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(G2rainRaindropPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<G2rainRaindropPo> list);

    /**
     * 根据ID更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(G2rainRaindropPo entity);

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
    int updateByVersion(G2rainRaindropPo entity);

    /**
     * 根据ID查询记录
     *
     * @param id 主键ID
     * @return 实体对象
     */
    G2rainRaindropPo selectById(Long id);

    /**
     * 根据查询入参DTO筛选列表
     *
     * @param selectDto 查询条件DTO
     * @return 实体对象列表
     */
    List<G2rainRaindropPo> selectList(G2rainRaindropSelectDto selectDto);

    /**
     * 根据业务标签 修改 ID 最大值
     *
     * @param bizTag 业务标签
     * @return 影响行数
     */
    int updateMaxId(@Param("bizTag") String bizTag);

    /**
     * 根据业务标签以及 步长 修改 ID 最大值
     *
     * @param bizTag 业务标签
     * @param step   步长
     * @return 影响行数
     */
    int updateMaxIdByCustomStep(@Param("bizTag") String bizTag, @Param("step") Integer step);

    /**
     * 查询业务标签列表
     *
     * @return 业务标签列表
     */
    List<String> selectAllBizTags();
}
