package com.g2rain.infra.converter;

import com.g2rain.common.converter.CommonConverter;
import com.g2rain.infra.dao.po.I18nMessageUsagePo;
import com.g2rain.infra.dto.I18nMessageUsageDto;
import com.g2rain.infra.vo.I18nMessageUsageVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 国际化信息用途表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: i18n_message_usage
 *
 * @author G2rain Generator
 */
@Mapper(uses = CommonConverter.class)
public interface I18nMessageUsageConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    I18nMessageUsageConverter INSTANCE = Mappers.getMapper(I18nMessageUsageConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    I18nMessageUsageVo po2vo(I18nMessageUsagePo po);

    /**
     * Dto -> Po
     * 自动将 createTime 和 updateTime 从 {@link String} 转换为 {@link LocalDateTime}
     * 忽略 version 字段
     */
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "stringToLocalDateTime")
    I18nMessageUsagePo dto2po(I18nMessageUsageDto dto);
}
