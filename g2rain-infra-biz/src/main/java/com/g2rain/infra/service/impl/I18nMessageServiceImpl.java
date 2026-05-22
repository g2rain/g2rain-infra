package com.g2rain.infra.service.impl;

import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.LocalizedErrorMessage;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.syncer.EventPublisherHub;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.infra.converter.I18nMessageConverter;
import com.g2rain.infra.dao.I18nMessageDao;
import com.g2rain.infra.dao.po.I18nMessagePo;
import com.g2rain.infra.dto.I18nMessageDto;
import com.g2rain.infra.dto.I18nMessageSelectDto;
import com.g2rain.infra.enums.I18nMsgUsage;
import com.g2rain.infra.enums.InfraSyncerEnum;
import com.g2rain.infra.service.I18nMessageService;
import com.g2rain.infra.utils.Constants;
import com.g2rain.infra.vo.I18nLocaleMessageVo;
import com.g2rain.infra.vo.I18nMessageVo;
import com.g2rain.infra.vo.I18nMsgUsageVo;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Resource
    private EventPublisherHub eventPublisherHub;

    @Resource
    private ObjectMapper objectMapper;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<I18nMessageVo> selectList(I18nMessageSelectDto selectDto) {
        return i18nMessageDao.selectList(selectDto)
            .stream()
            .map(I18nMessageConverter.INSTANCE::po2vo)
            .toList();
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
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    public Long save(I18nMessageDto dto) {
        // 校验扩展字段是否合法
        if (Objects.nonNull(dto.getExtendField())) {
            // 设置 null 不然JSON格式的数据库字段类型会抱错
            if (dto.getExtendField().isBlank()) {
                dto.setExtendField(null);
            } else {
                // 校验是否为合法的 JSON 格式
                try {
                    objectMapper.readTree(dto.getExtendField());
                } catch (Exception e) {
                    throw new BusinessException(
                        SystemErrorCode.PARAM_VAL_INVALID,
                        "extendField"
                    );
                }
            }
        }

        I18nMsgUsage i18nMsgUsage = I18nMsgUsage.fromName(dto.getMessageUsageCode());
        Asserts.isTrue(Objects.nonNull(i18nMsgUsage),
            SystemErrorCode.PARAM_VAL_INVALID, "messageUsageCode"
        );

        // 页面文案, 标签必填
        if (i18nMsgUsage.equals(I18nMsgUsage.UI_MESSAGE)) {
            Asserts.isTrue(Objects.nonNull(dto.getTag()), SystemErrorCode.PARAM_REQUIRED, "tag");
        } else {// 非页面文案, 不需要设置标签
            dto.setTag(null);
        }

        boolean exists = Arrays.asList(Locale.getAvailableLocales()).contains(
            new Locale.Builder().setLanguage(dto.getLanguageCode()).setRegion(dto.getRegionCode()).build()
        );
        Asserts.isTrue(exists, SystemErrorCode.PARAM_VAL_INVALID, "languageCode or regionCode");

        // 校验国际化信息是否重复
        I18nMessageSelectDto selectDto = new I18nMessageSelectDto();
        selectDto.setTag(dto.getTag());
        selectDto.setLanguageCode(dto.getLanguageCode());
        selectDto.setMatchEmptyRegionCode(true);
        selectDto.setRegionCode(dto.getRegionCode());
        selectDto.setMessageUsageCode(dto.getMessageUsageCode());
        selectDto.setMessageCode(dto.getMessageCode());
        List<I18nMessagePo> i18nMessages = i18nMessageDao.selectList(selectDto);
        if (i18nMessages.stream().anyMatch(o -> !Objects.equals(o.getId(), dto.getId()))) {
            throw new BusinessException(SystemErrorCode.DATA_EXISTS);
        }

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

        // 删除国际化信息
        int total = i18nMessageDao.delete(id);

        // 只有错误信息国际化需要推送广播
        if (I18nMsgUsage.ERROR_CODE.name().equals(entity.getMessageUsageCode())) {
            eventPublisherHub.sendDelete(
                Constants.SYNC_OUTPUT_BINDING,
                InfraSyncerEnum.ERROR_MSG.name(),
                toLocalizedErrorMessage(entity)
            );
        }

        return total;
    }

    @Override
    public List<I18nMsgUsageVo> i18nMessageUsages() {
        return Arrays.stream(I18nMsgUsage.values())
            .map(usage -> new I18nMsgUsageVo(usage.name(), usage.getDesc()))
            .collect(Collectors.toList());
    }

    @Override
    public List<String> tagDict() {
        return i18nMessageDao.selectAllTags();
    }

    @Override
    public List<I18nLocaleMessageVo> i18nMessageLocale(I18nMessageSelectDto selectDto) {
        return i18nMessageDao.selectList(selectDto)
            .stream()
            .map(I18nMessageConverter.INSTANCE::po2locale)
            .toList();
    }

    /**
     * 组装 `国际化错误信息` 广播消息
     *
     * @param entity 国际化错误信息
     * @return `国际化错误信息` 广播消息
     */
    private LocalizedErrorMessage toLocalizedErrorMessage(I18nMessagePo entity) {
        String locale = new Locale.Builder()
            .setLanguage(entity.getLanguageCode())
            .setRegion(entity.getRegionCode())
            .build()
            .toLanguageTag();
        LocalizedErrorMessage msg = new LocalizedErrorMessage();
        msg.setErrorCode(entity.getMessageCode());
        msg.setLocale(locale);
        msg.setMessageTemplate(entity.getMessageText());
        return msg;
    }
}
