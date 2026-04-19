package com.g2rain.infra.converter;

import com.g2rain.common.converter.CommonConverter;
import com.g2rain.infra.dao.po.G2rainRaindropPo;
import com.g2rain.infra.dto.G2rainRaindropDto;
import com.g2rain.infra.vo.G2rainRaindropVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 全局唯一ID管理表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: g2rain_raindrop
 *
 * @author G2rain Generator
 */
@Mapper(uses = CommonConverter.class)
public interface G2rainRaindropConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    G2rainRaindropConverter INSTANCE = Mappers.getMapper(G2rainRaindropConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    G2rainRaindropVo po2vo(G2rainRaindropPo po);

    /**
     * Dto -> Po
     * 自动将 createTime 和 updateTime 从 {@link String} 转换为 {@link LocalDateTime}
     * 忽略 version 字段
     */
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "stringToLocalDateTime")
    G2rainRaindropPo dto2po(G2rainRaindropDto dto);
}
