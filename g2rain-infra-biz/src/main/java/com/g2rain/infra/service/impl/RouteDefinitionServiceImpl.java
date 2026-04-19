package com.g2rain.infra.service.impl;

import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.syncer.EventPublisherHub;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.infra.converter.RouteDefinitionConverter;
import com.g2rain.infra.dao.RouteDefinitionDao;
import com.g2rain.infra.dao.po.RouteDefinitionPo;
import com.g2rain.infra.dto.RouteDefinitionDto;
import com.g2rain.infra.dto.RouteDefinitionSelectDto;
import com.g2rain.infra.enums.InfraSyncerEnum;
import com.g2rain.infra.service.RouteDefinitionService;
import com.g2rain.infra.utils.Constants;
import com.g2rain.infra.vo.RouteDefinitionVo;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 网关路由表服务实现类
 * 表名: route_definition
 *
 * @author G2rain Generator
 */
@Service(value = "routeDefinitionServiceImpl")
public class RouteDefinitionServiceImpl implements RouteDefinitionService {

    @Resource(name = "routeDefinitionDao")
    private RouteDefinitionDao routeDefinitionDao;

    @Resource
    private EventPublisherHub eventPublisherHub;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<RouteDefinitionVo> selectList(RouteDefinitionSelectDto selectDto) {
        return routeDefinitionDao.selectList(selectDto)
            .stream()
            .map(RouteDefinitionConverter.INSTANCE::po2vo)
            .toList();
    }

    @Override
    public PageData<RouteDefinitionVo> selectPage(PageSelectListDto<RouteDefinitionSelectDto> selectDto) {
        Page<RouteDefinitionPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            routeDefinitionDao.selectList(selectDto.getQuery())
        );

        List<RouteDefinitionVo> result = page.getResult()
            .stream()
            .map(RouteDefinitionConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    @Transactional
    public Long save(RouteDefinitionDto dto) {
        // 转换DTO 为 PO
        RouteDefinitionPo entity = RouteDefinitionConverter.INSTANCE.dto2po(dto);

        // 判断是新增还是更新
        Long id = entity.getId();
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = routeDefinitionDao.insert(entity);

            // 广播新增路由
            eventPublisherHub.sendCreate(
                Constants.SYNC_OUTPUT_BINDING,
                InfraSyncerEnum.ROUTE_DEFINE.name(),
                RouteDefinitionConverter.INSTANCE.po2vo(entity)
            );

            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            // 更新：直接更新
            entity.setUpdateTime(Moments.now());
            int success = routeDefinitionDao.update(entity);

            // 广播修改路由
            eventPublisherHub.sendUpdate(
                Constants.SYNC_OUTPUT_BINDING,
                InfraSyncerEnum.ROUTE_DEFINE.name(),
                RouteDefinitionConverter.INSTANCE.po2vo(entity)
            );

            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        }

        return entity.getId();
    }

    @Override
    @Transactional
    public int delete(Long id) {
        RouteDefinitionPo entity = routeDefinitionDao.selectById(id);
        // `路由` 不存在
        Asserts.isTrue(Objects.nonNull(entity), SystemErrorCode.PARAM_VAL_INVALID, id);

        int total = routeDefinitionDao.delete(id);

        // 广播删除路由
        eventPublisherHub.sendDelete(
            Constants.SYNC_OUTPUT_BINDING,
            InfraSyncerEnum.ROUTE_DEFINE.name(),
            RouteDefinitionConverter.INSTANCE.po2vo(entity)
        );

        return total;
    }
}
