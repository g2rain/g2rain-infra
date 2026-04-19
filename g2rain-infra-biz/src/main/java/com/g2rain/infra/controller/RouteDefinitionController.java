package com.g2rain.infra.controller;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.infra.api.RouteDefinitionApi;
import com.g2rain.infra.dto.RouteDefinitionDto;
import com.g2rain.infra.dto.RouteDefinitionSelectDto;
import com.g2rain.infra.service.RouteDefinitionService;
import com.g2rain.infra.vo.RouteDefinitionVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 网关路由表控制器
 * 表名: route_definition
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/route_definition")
public class RouteDefinitionController implements RouteDefinitionApi {

    @Resource(name = "routeDefinitionServiceImpl")
    private RouteDefinitionService routeDefinitionService;

    @Override
    public Result<List<RouteDefinitionVo>> selectList(RouteDefinitionSelectDto selectDto) {
        return Result.success(routeDefinitionService.selectList(selectDto));
    }

    @Override
    public Result<PageData<RouteDefinitionVo>> selectPage(PageSelectListDto<RouteDefinitionSelectDto> selectDto) {
        return Result.successPage(routeDefinitionService.selectPage(selectDto));
    }

    /**
     * 新增或更新网关路由
     *
     * @param dto 网关路由数据传输对象
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新网关路由", description = "新增或更新网关路由配置信息")
    public Result<Long> save(@RequestBody RouteDefinitionDto dto) {
        return Result.success(routeDefinitionService.save(dto));
    }

    /**
     * 根据主键删除网关路由记录
     *
     * @param id 网关路由主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除网关路由记录", description = "根据主键删除网关路由记录")
    public Result<Integer> delete(@Parameter(description = "网关路由标识") @PathVariable Long id) {
        return Result.success(routeDefinitionService.delete(id));
    }
}
