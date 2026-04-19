package com.g2rain.infra.service.impl;

import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.infra.converter.DictionaryUsageConverter;
import com.g2rain.infra.dao.DictionaryUsageDao;
import com.g2rain.infra.dao.po.DictionaryUsagePo;
import com.g2rain.infra.dto.DictionaryUsageDto;
import com.g2rain.infra.dto.DictionaryUsageSelectDto;
import com.g2rain.infra.service.DictionaryUsageService;
import com.g2rain.infra.vo.DictionaryUsageVo;
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
 * 字典用途表服务实现类
 * 表名: dictionary_usage
 *
 * @author G2rain Generator
 */
@Service(value = "dictionaryUsageServiceImpl")
public class DictionaryUsageServiceImpl implements DictionaryUsageService {

    @Resource(name = "dictionaryUsageDao")
    private DictionaryUsageDao dictionaryUsageDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<DictionaryUsageVo> selectList(DictionaryUsageSelectDto selectDto) {
        return dictionaryUsageDao.selectList(selectDto)
            .stream()
            .map(DictionaryUsageConverter.INSTANCE::po2vo)
            .toList();
    }

    @Override
    public PageData<DictionaryUsageVo> selectPage(PageSelectListDto<DictionaryUsageSelectDto> selectDto) {
        Page<DictionaryUsagePo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            dictionaryUsageDao.selectList(selectDto.getQuery())
        );

        List<DictionaryUsageVo> result = page.getResult()
            .stream()
            .map(DictionaryUsageConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    public Long save(DictionaryUsageDto dto) {
        // 转换DTO为PO
        DictionaryUsagePo entity = DictionaryUsageConverter.INSTANCE.dto2po(dto);

        // 判断是新增还是更新
        Long id = entity.getId();
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = dictionaryUsageDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            // 更新：直接更新
            entity.setUpdateTime(Moments.now());
            int success = dictionaryUsageDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        }

        return entity.getId();
    }

    @Override
    public int delete(Long id) {
        return dictionaryUsageDao.delete(id);
    }
}
