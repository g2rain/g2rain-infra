package com.g2rain.infra.service.impl;

import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.utils.Strings;
import com.g2rain.infra.converter.DictionaryItemConverter;
import com.g2rain.infra.dao.DictionaryItemDao;
import com.g2rain.infra.dao.DictionaryUsageDao;
import com.g2rain.infra.dao.po.DictionaryItemPo;
import com.g2rain.infra.dao.po.DictionaryUsagePo;
import com.g2rain.infra.dto.DictionaryItemDto;
import com.g2rain.infra.dto.DictionaryItemSelectDto;
import com.g2rain.infra.dto.DictionaryItemTreeSelectDto;
import com.g2rain.infra.dto.DictionaryUsageSelectDto;
import com.g2rain.infra.service.DictionaryItemService;
import com.g2rain.infra.vo.DictionaryItemTreeVo;
import com.g2rain.infra.vo.DictionaryItemVo;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 字典明细表服务实现类
 * 表名: dictionary_item
 *
 * @author G2rain Generator
 */
@Service(value = "dictionaryItemServiceImpl")
public class DictionaryItemServiceImpl implements DictionaryItemService {

    @Resource(name = "dictionaryItemDao")
    private DictionaryItemDao dictionaryItemDao;

    @Resource(name = "dictionaryUsageDao")
    private DictionaryUsageDao dictionaryUsageDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<DictionaryItemVo> selectList(DictionaryItemSelectDto selectDto) {
        if (!resolveDictionaryUsageId(selectDto)) {
            return List.of();
        }
        List<DictionaryItemVo> result = dictionaryItemDao.selectList(selectDto)
            .stream()
            .map(DictionaryItemConverter.INSTANCE::po2vo)
            .toList();
        fillParentName(result);
        return result;
    }

    @Override
    public PageData<DictionaryItemVo> selectPage(PageSelectListDto<DictionaryItemSelectDto> selectDto) {
        if (!resolveDictionaryUsageId(selectDto.getQuery())) {
            return PageData.of(selectDto.getPageNum(), selectDto.getPageSize(), 0L, List.of());
        }
        Page<DictionaryItemPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            dictionaryItemDao.selectList(selectDto.getQuery())
        );

        List<DictionaryItemVo> result = page.getResult()
            .stream()
            .map(DictionaryItemConverter.INSTANCE::po2vo)
            .toList();
        fillParentName(result);
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    public Long save(DictionaryItemDto dto) {
        // 转换DTO为PO
        DictionaryItemPo entity = DictionaryItemConverter.INSTANCE.dto2po(dto);

        // 判断是新增还是更新
        Long id = entity.getId();
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = dictionaryItemDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            // 更新：直接更新
            entity.setUpdateTime(Moments.now());
            int success = dictionaryItemDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        }

        return entity.getId();
    }

    @Override
    public int delete(Long id) {
        return dictionaryItemDao.delete(id);
    }

    /**
     * 按用途查出全部未删除字典项，在内存中按 {@code parentId} 组装为多叉树。
     * 根节点：{@code parentId} 为 {@code null} 或 {@code 0}；父节点不在本次结果中的节点视为孤儿，挂到根层，避免丢失。
     */
    @Override
    public List<DictionaryItemTreeVo> selectTree(DictionaryItemTreeSelectDto selectDto) {
        DictionaryItemSelectDto query = new DictionaryItemSelectDto();
        query.setDictionaryUsageId(selectDto.getDictionaryUsageId());
        List<DictionaryItemPo> poList = dictionaryItemDao.selectList(query);
        if (poList.isEmpty()) {
            return List.of();
        }
        List<DictionaryItemTreeVo> nodes = poList.stream()
            .map(DictionaryItemConverter.INSTANCE::po2treeVo)
            .toList();
        // id -> 节点，用于 O(1) 查找父节点
        Map<Long, DictionaryItemTreeVo> byId = HashMap.newHashMap(nodes.size());
        for (DictionaryItemTreeVo node : nodes) {
            byId.put(node.getId(), node);
        }
        List<DictionaryItemTreeVo> roots = new ArrayList<>();
        for (DictionaryItemTreeVo node : nodes) {
            Long parentId = node.getParentId();
            if (parentId == null || parentId == 0L) {
                node.setParentName(null);
                roots.add(node);
                continue;
            }
            DictionaryItemTreeVo parent = byId.get(parentId);
            if (parent == null) {
                // 父节点未返回（已删或数据不一致）：与根同级返回，便于排查
                node.setParentName(null);
                roots.add(node);
                continue;
            }
            node.setParentName(parent.getName());
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            parent.getChildren().add(node);
        }
        // 每一层：sortIndex 升序，相同则按 id；null sortIndex 排在后面
        Comparator<DictionaryItemTreeVo> order = Comparator
            .comparing(DictionaryItemTreeVo::getSortIndex, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(DictionaryItemTreeVo::getId, Comparator.nullsLast(Comparator.naturalOrder()));
        sortTreeLevel(roots, order);
        return roots;
    }

    /** 对当前层及所有子层按同一比较器排序（深度优先）。 */
    private void sortTreeLevel(List<DictionaryItemTreeVo> level, Comparator<DictionaryItemTreeVo> order) {
        if (level == null || level.isEmpty()) {
            return;
        }
        level.sort(order);
        for (DictionaryItemTreeVo node : level) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                sortTreeLevel(node.getChildren(), order);
            }
        }
    }

    private void fillParentName(List<? extends DictionaryItemVo> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Map<Long, String> nameById = HashMap.newHashMap(list.size());
        for (DictionaryItemVo item : list) {
            if (item.getId() != null) {
                nameById.put(item.getId(), item.getName());
            }
        }
        Set<Long> missingParentIds = new HashSet<>();
        for (DictionaryItemVo item : list) {
            Long parentId = item.getParentId();
            if (parentId == null || parentId == 0L) {
                item.setParentName(null);
                continue;
            }
            if (!nameById.containsKey(parentId)) {
                missingParentIds.add(parentId);
            }
        }
        if (!missingParentIds.isEmpty()) {
            DictionaryItemSelectDto parentQuery = new DictionaryItemSelectDto();
            parentQuery.setIds(missingParentIds);
            List<DictionaryItemPo> parentList = dictionaryItemDao.selectList(parentQuery);
            for (DictionaryItemPo parent : parentList) {
                nameById.put(parent.getId(), parent.getName());
            }
            for (Long missingParentId : missingParentIds) {
                nameById.putIfAbsent(missingParentId, null);
            }
        }
        for (DictionaryItemVo item : list) {
            Long parentId = item.getParentId();
            if (parentId == null || parentId == 0L) {
                continue;
            }
            String parentName = nameById.get(parentId);
            item.setParentName(parentName);
        }
    }

    private boolean resolveDictionaryUsageId(DictionaryItemSelectDto selectDto) {
        if (selectDto == null || Strings.isBlank(selectDto.getUsageCode())) {
            return true;
        }
        if (selectDto.getDictionaryUsageId() != null && selectDto.getDictionaryUsageId() != 0L) {
            return true;
        }
        DictionaryUsageSelectDto usageQuery = new DictionaryUsageSelectDto();
        usageQuery.setUsageCode(selectDto.getUsageCode());
        List<DictionaryUsagePo> usageList = dictionaryUsageDao.selectList(usageQuery);
        if (usageList.isEmpty()) {
            return false;
        }
        selectDto.setDictionaryUsageId(usageList.getFirst().getId());
        return true;
    }
}
