package com.g2rain.infra.service.impl;

import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.infra.converter.I18nMessageUsageConverter;
import com.g2rain.infra.dao.I18nMessageUsageDao;
import com.g2rain.infra.dao.po.I18nMessageUsagePo;
import com.g2rain.infra.dto.I18nMessageUsageDto;
import com.g2rain.infra.dto.I18nMessageUsageSelectDto;
import com.g2rain.infra.service.I18nMessageUsageService;
import com.g2rain.infra.vo.I18nMessageUsageVo;
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
 * 国际化信息用途表服务实现类
 * 表名: i18n_message_usage
 *
 * @author G2rain Generator
 */
@Service(value = "i18nMessageUsageServiceImpl")
public class I18nMessageUsageServiceImpl implements I18nMessageUsageService {

    @Resource(name = "i18nMessageUsageDao")
    private I18nMessageUsageDao i18nMessageUsageDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<I18nMessageUsageVo> selectList(I18nMessageUsageSelectDto selectDto) {
        return i18nMessageUsageDao.selectList(selectDto)
            .stream()
            .map(I18nMessageUsageConverter.INSTANCE::po2vo)
            .toList();
    }

    @Override
    public PageData<I18nMessageUsageVo> selectPage(PageSelectListDto<I18nMessageUsageSelectDto> selectDto) {
        Page<I18nMessageUsagePo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            i18nMessageUsageDao.selectList(selectDto.getQuery())
        );

        List<I18nMessageUsageVo> result = page.getResult()
            .stream()
            .map(I18nMessageUsageConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    public Long save(I18nMessageUsageDto dto) {
        // 转换DTO为PO
        I18nMessageUsagePo entity = I18nMessageUsageConverter.INSTANCE.dto2po(dto);

        // 判断是新增还是更新
        Long id = entity.getId();
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = i18nMessageUsageDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            // 更新：直接更新
            entity.setUpdateTime(Moments.now());
            int success = i18nMessageUsageDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        }

        return entity.getId();
    }

    @Override
    public int delete(Long id) {
        return i18nMessageUsageDao.delete(id);
    }
}
