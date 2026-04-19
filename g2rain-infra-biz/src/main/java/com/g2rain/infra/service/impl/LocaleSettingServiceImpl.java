package com.g2rain.infra.service.impl;

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
import com.g2rain.infra.vo.LocaleSettingVo;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 地域-语言设置表服务实现类
 * 表名: locale_setting
 *
 * @author G2rain Generator
 */
@Service(value = "localeSettingServiceImpl")
public class LocaleSettingServiceImpl implements LocaleSettingService {

    @Resource(name = "localeSettingDao")
    private LocaleSettingDao localeSettingDao;

    private IdGenerator idGenerator;

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
        // 转换DTO为PO
        LocaleSettingPo entity = LocaleSettingConverter.INSTANCE.dto2po(dto);

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
}
