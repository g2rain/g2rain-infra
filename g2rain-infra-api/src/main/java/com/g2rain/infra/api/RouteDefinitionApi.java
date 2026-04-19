package com.g2rain.infra.api;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.infra.dto.RouteDefinitionSelectDto;
import com.g2rain.infra.vo.RouteDefinitionVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 网关路由表API接口
 * 表名: route_definition
 *
 * @author G2rain Generator
 */
@Tag(name = "网关路由", description = "网关路由表相关接口")
public interface RouteDefinitionApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询网关路由列表", description = "根据查询条件返回网关路由列表")
    Result<List<RouteDefinitionVo>> selectList(RouteDefinitionSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询网关路由列表", description = "分页查询网关路由列表")
    Result<PageData<RouteDefinitionVo>> selectPage(PageSelectListDto<RouteDefinitionSelectDto> selectDto);
}
