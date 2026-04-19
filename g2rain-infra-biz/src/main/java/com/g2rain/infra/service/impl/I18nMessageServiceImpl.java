package com.g2rain.infra.service.impl;

import com.g2rain.common.exception.LocalizedErrorMessage;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.syncer.EventPublisherHub;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.utils.Strings;
import com.g2rain.infra.converter.I18nMessageConverter;
import com.g2rain.infra.dao.I18nMessageDao;
import com.g2rain.infra.dao.I18nMessageUsageDao;
import com.g2rain.infra.dao.po.I18nMessagePo;
import com.g2rain.infra.dao.po.I18nMessageUsagePo;
import com.g2rain.infra.dto.I18nMessageDto;
import com.g2rain.infra.dto.I18nMessageSelectDto;
import com.g2rain.infra.dto.I18nMessageUsageSelectDto;
import com.g2rain.infra.enums.InfraSyncerEnum;
import com.g2rain.infra.service.I18nMessageService;
import com.g2rain.infra.utils.Constants;
import com.g2rain.infra.vo.I18nMessageVo;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 国际化信息表服务实现类
 * 表名: i18n_message
 *
 * @author G2rain Generator
 */
@Service(value = "i18nMessageServiceImpl")
public class I18nMessageServiceImpl implements I18nMessageService {

    @Resource(name = "i18nMessageDao")
    private I18nMessageDao i18nMessageDao;

    @Resource(name = "i18nMessageUsageDao")
    private I18nMessageUsageDao i18nMessageUsageDao;

    @Resource
    private EventPublisherHub eventPublisherHub;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<I18nMessageVo> selectList(I18nMessageSelectDto selectDto) {
        List<I18nMessageVo> result = i18nMessageDao.selectList(selectDto)
            .stream()
            .map(I18nMessageConverter.INSTANCE::po2vo)
            .toList();
        fillMessageUsageName(result);
        return result;
    }

    @Override
    public PageData<I18nMessageVo> selectPage(PageSelectListDto<I18nMessageSelectDto> selectDto) {
        Page<I18nMessagePo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            i18nMessageDao.selectList(selectDto.getQuery())
        );

        List<I18nMessageVo> result = page.getResult()
            .stream()
            .map(I18nMessageConverter.INSTANCE::po2vo)
            .toList();
        fillMessageUsageName(result);
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    public Long save(I18nMessageDto dto) {
        // 转换DTO为 PO
        I18nMessagePo entity = I18nMessageConverter.INSTANCE.dto2po(dto);

        // 判断是新增还是更新
        Long id = entity.getId();
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = i18nMessageDao.insert(entity);

            // 广播新增错误信息
            eventPublisherHub.sendCreate(
                Constants.SYNC_OUTPUT_BINDING,
                InfraSyncerEnum.ERROR_MSG.name(),
                toLocalizedErrorMessage(entity)
            );

            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            // 更新：直接更新
            entity.setUpdateTime(Moments.now());
            int success = i18nMessageDao.update(entity);

            // 广播修改错误信息
            eventPublisherHub.sendUpdate(
                Constants.SYNC_OUTPUT_BINDING,
                InfraSyncerEnum.ERROR_MSG.name(),
                toLocalizedErrorMessage(entity)
            );

            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        }

        return entity.getId();
    }

    @Override
    @Transactional
    public int delete(Long id) {
        I18nMessagePo entity = i18nMessageDao.selectById(id);
        // `消息` 不存在
        Asserts.isTrue(Objects.nonNull(entity), SystemErrorCode.PARAM_VAL_INVALID, id);

        int total = i18nMessageDao.delete(id);

        // 广播删除错误信息
        eventPublisherHub.sendDelete(
            Constants.SYNC_OUTPUT_BINDING,
            InfraSyncerEnum.ERROR_MSG.name(),
            toLocalizedErrorMessage(entity)
        );

        return total;
    }

    private LocalizedErrorMessage toLocalizedErrorMessage(I18nMessagePo entity) {
        LocalizedErrorMessage msg = new LocalizedErrorMessage();
        msg.setErrorCode(entity.getMessageCode());
        String locale = entity.getLanguageCode();
        String region = entity.getRegionCode();
        if (Strings.isNotBlank(region)) {
            locale = locale + "_" + region;
        }

        msg.setLocale(locale);
        msg.setMessageTemplate(entity.getMessageText());
        return msg;
    }


    private void fillMessageUsageName(List<I18nMessageVo> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Map<Long, String> usageNameById = HashMap.newHashMap(list.size());
        Set<Long> missingUsageIds = new HashSet<>();
        for (I18nMessageVo item : list) {
            Long messageUsageId = item.getMessageUsageId();
            if (messageUsageId != null && messageUsageId != 0L) {
                missingUsageIds.add(messageUsageId);
            } else {
                item.setMessageUsageName(null);
            }
        }
        if (!missingUsageIds.isEmpty()) {
            I18nMessageUsageSelectDto usageQuery = new I18nMessageUsageSelectDto();
            usageQuery.setIds(missingUsageIds);
            List<I18nMessageUsagePo> usageList = i18nMessageUsageDao.selectList(usageQuery);
            for (I18nMessageUsagePo usage : usageList) {
                usageNameById.put(usage.getId(), usage.getName());
            }
            for (Long missingUsageId : missingUsageIds) {
                usageNameById.putIfAbsent(missingUsageId, null);
            }
        }
        for (I18nMessageVo item : list) {
            Long messageUsageId = item.getMessageUsageId();
            if (messageUsageId == null || messageUsageId == 0L) {
                continue;
            }
            item.setMessageUsageName(usageNameById.get(messageUsageId));
        }
    }
}
