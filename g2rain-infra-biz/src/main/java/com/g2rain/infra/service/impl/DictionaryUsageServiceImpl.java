package com.g2rain.infra.service.impl;

import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.infra.converter.DictionaryUsageConverter;
import com.g2rain.infra.dao.DictionaryItemDao;
import com.g2rain.infra.dao.DictionaryUsageDao;
import com.g2rain.infra.dao.po.DictionaryUsagePo;
import com.g2rain.infra.dto.DictionaryItemSelectDto;
import com.g2rain.infra.dto.DictionaryUsageDto;
import com.g2rain.infra.dto.DictionaryUsageSelectDto;
import com.g2rain.infra.enums.InfraErrorCode;
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

    @Resource(name = "dictionaryItemDao")
    private DictionaryItemDao dictionaryItemDao;

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
        DictionaryUsageSelectDto selectDto = new DictionaryUsageSelectDto();
        selectDto.setUsageCode(dto.getUsageCode());
        List<DictionaryUsagePo> usages = dictionaryUsageDao.selectList(selectDto);
        if (usages.stream().anyMatch(o -> !Objects.equals(o.getId(), dto.getId()))) {
            throw new BusinessException(SystemErrorCode.DATA_EXISTS);
        }

        // 转换 DTO 为 PO
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
        // 检查字典是否存在
        DictionaryUsagePo raindrop = dictionaryUsageDao.selectById(id);
        Asserts.isTrue(Objects.nonNull(raindrop), SystemErrorCode.PARAM_VAL_INVALID, id);

        // 如果存在 字典明细, 字典不允许删除
        DictionaryItemSelectDto selectDto = new DictionaryItemSelectDto();
        selectDto.setUsageCode(raindrop.getUsageCode());
        Long total = dictionaryItemDao.checkDictItemExists(selectDto);
        Asserts.lessThanOrEqual(total, 0, InfraErrorCode.DEL_DICT_USAGE_ILLEGAL);

        // 删除字典用途记录
        return dictionaryUsageDao.delete(id);
    }
}
