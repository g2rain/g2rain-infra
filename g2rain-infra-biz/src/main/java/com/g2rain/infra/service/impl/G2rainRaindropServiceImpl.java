package com.g2rain.infra.service.impl;

import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.infra.converter.G2rainRaindropConverter;
import com.g2rain.infra.dao.G2rainRaindropDao;
import com.g2rain.infra.dao.po.G2rainRaindropPo;
import com.g2rain.infra.dto.G2rainRaindropDto;
import com.g2rain.infra.dto.G2rainRaindropSelectDto;
import com.g2rain.infra.enums.KeysmithType;
import com.g2rain.infra.service.G2rainRaindropService;
import com.g2rain.infra.service.Keysmith;
import com.g2rain.infra.vo.G2rainRaindropVo;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 全局唯一ID管理表服务实现类
 * 表名: g2rain_raindrop
 *
 * @author G2rain Generator
 */
@Service(value = "g2rainRaindropServiceImpl")
public class G2rainRaindropServiceImpl implements G2rainRaindropService {
    /**
     * 取号处理器集合
     */
    private final Map<KeysmithType, Keysmith> keysmithHandlers;

    @Resource(name = "g2rainRaindropDao")
    private G2rainRaindropDao g2rainRaindropDao;

    public G2rainRaindropServiceImpl(List<Keysmith> keysmithList) {
        keysmithHandlers = keysmithList.stream().collect(
            Collectors.toMap(Keysmith::type, Function.identity())
        );
    }

    @Override
    public List<G2rainRaindropVo> selectList(G2rainRaindropSelectDto selectDto) {
        return g2rainRaindropDao.selectList(selectDto)
            .stream()
            .map(G2rainRaindropConverter.INSTANCE::po2vo)
            .toList();
    }

    @Override
    public PageData<G2rainRaindropVo> selectPage(PageSelectListDto<G2rainRaindropSelectDto> selectDto) {
        Page<G2rainRaindropPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            g2rainRaindropDao.selectList(selectDto.getQuery())
        );

        List<G2rainRaindropVo> result = page.getResult()
            .stream()
            .map(G2rainRaindropConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    public Long save(G2rainRaindropDto dto) {
        // 转换DTO为PO
        G2rainRaindropPo entity = G2rainRaindropConverter.INSTANCE.dto2po(dto);

        // 判断是新增还是更新
        Long id = entity.getId();
        if (Objects.isNull(id) || id == 0) {
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = g2rainRaindropDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            // 更新：直接更新
            entity.setUpdateTime(Moments.now());
            int success = g2rainRaindropDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        }

        return entity.getId();
    }

    @Override
    public int delete(Long id) {
        return g2rainRaindropDao.delete(id);
    }

    @Override
    public Long allocate(KeysmithType type, String bizTag) {
        return keysmithHandlers.get(type).allocate(bizTag);
    }
}
