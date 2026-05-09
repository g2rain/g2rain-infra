package com.g2rain.infra.service.impl;

import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.infra.converter.LocaleSettingConverter;
import com.g2rain.infra.dao.LocaleSettingDao;
import com.g2rain.infra.dao.po.LocaleSettingPo;
import com.g2rain.infra.dto.LocaleSettingDto;
import com.g2rain.infra.dto.LocaleSettingSelectDto;
import com.g2rain.infra.service.LocaleSettingService;
import com.g2rain.infra.vo.LocaleCodeNameVo;
import com.g2rain.infra.vo.LocaleSettingVo;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 地域-语言设置表服务实现类
 * 表名: locale_setting
 *
 * @author G2rain Generator
 */
@Service(value = "localeSettingServiceImpl")
public class LocaleSettingServiceImpl implements LocaleSettingService {

    private final Set<String> LOCALE_DICT;

    @Resource(name = "localeSettingDao")
    private LocaleSettingDao localeSettingDao;

    private IdGenerator idGenerator;

    /**
     * 初始化区域-语言集合
     */
    public LocaleSettingServiceImpl() {
        this.LOCALE_DICT = Arrays.stream(Locale.getAvailableLocales())
            .filter(l -> !l.getLanguage().isBlank() && !l.getCountry().isBlank())
            .map(l -> l.getLanguage() + "-" + l.getCountry())
            .collect(Collectors.toCollection(TreeSet::new));
    }

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<LocaleSettingVo> selectList(LocaleSettingSelectDto selectDto) {
        return localeSettingDao.selectList(selectDto)
            .stream()
            .map(LocaleSettingConverter.INSTANCE::po2vo)
            .toList();
    }

    @Override
    public PageData<LocaleSettingVo> selectPage(PageSelectListDto<LocaleSettingSelectDto> selectDto) {
        Page<LocaleSettingPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            localeSettingDao.selectList(selectDto.getQuery())
        );

        List<LocaleSettingVo> result = page.getResult()
            .stream()
            .map(LocaleSettingConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    public Long save(LocaleSettingDto dto) {
        // 校验区域-语言编码是否合法
        Asserts.isTrue(LOCALE_DICT.contains(dto.getCode()),
            SystemErrorCode.PARAM_VAL_INVALID, "code"
        );

        // 校验字典用途编码是否存在
        LocaleSettingSelectDto selectDto = new LocaleSettingSelectDto();
        selectDto.setCode(dto.getCode());
        List<LocaleSettingPo> localeSettings = localeSettingDao.selectList(selectDto);
        if (localeSettings.stream().anyMatch(o -> !Objects.equals(o.getId(), dto.getId()))) {
            throw new BusinessException(SystemErrorCode.DATA_EXISTS);
        }

        // 转换 DTO 为 PO
        LocaleSettingPo entity = LocaleSettingConverter.INSTANCE.dto2po(dto);
        Locale locale = Locale.forLanguageTag(dto.getCode());
        entity.setLanguageCode(locale.getLanguage());
        entity.setRegionCode(locale.getCountry());

        // 判断是新增还是更新
        Long id = entity.getId();
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = localeSettingDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            // 更新：直接更新
            entity.setUpdateTime(Moments.now());
            int success = localeSettingDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        }

        return entity.getId();
    }

    @Override
    public int delete(Long id) {
        return localeSettingDao.delete(id);
    }

    /**
     * 获取地域-语言字典
     *
     * @return 地域-语言字典集合
     */
    @Override
    public Set<String> localeDict() {
        return this.LOCALE_DICT;
    }

    @Override
    public Map<String, Set<String>> getLanguageCountries() {
        return Arrays.stream(Locale.getAvailableLocales())
            .filter(l -> !l.getLanguage().isBlank() && !l.getCountry().isBlank())
            .collect(Collectors.groupingBy(
                Locale::getLanguage,
                Collectors.mapping(Locale::getCountry, Collectors.toCollection(TreeSet::new))
            ));
    }

    @Override
    public List<LocaleCodeNameVo> code2name() {
        return localeSettingDao.selectList(new LocaleSettingSelectDto())
            .stream()
            .map(o -> new LocaleCodeNameVo(o.getCode(), o.getName()))
            .toList();
    }
}
