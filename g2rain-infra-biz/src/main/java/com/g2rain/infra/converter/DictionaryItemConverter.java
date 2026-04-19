package com.g2rain.infra.converter;

import com.g2rain.common.converter.CommonConverter;
import com.g2rain.infra.dao.po.DictionaryItemPo;
import com.g2rain.infra.dto.DictionaryItemDto;
import com.g2rain.infra.vo.DictionaryItemTreeVo;
import com.g2rain.infra.vo.DictionaryItemVo;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 字典明细表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: dictionary_item
 *
 * @author G2rain Generator
 */
@Mapper(uses = CommonConverter.class)
public interface DictionaryItemConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    DictionaryItemConverter INSTANCE = Mappers.getMapper(DictionaryItemConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "parentName", ignore = true)
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    DictionaryItemVo po2vo(DictionaryItemPo po);

    /**
     * Po -> 树节点 VO（继承 {@link #po2vo} 的字段映射；children 在组装树时填充）
     */
    @InheritConfiguration(name = "po2vo")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "parentName", ignore = true)
    DictionaryItemTreeVo po2treeVo(DictionaryItemPo po);

    /**
     * Dto -> Po
     * 自动将 createTime 和 updateTime 从 {@link String} 转换为 {@link LocalDateTime}
     * 忽略 version 字段
     * 忽略 deleteFlag 字段
     */
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleteFlag", ignore = true)
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "stringToLocalDateTime")
    DictionaryItemPo dto2po(DictionaryItemDto dto);
}
