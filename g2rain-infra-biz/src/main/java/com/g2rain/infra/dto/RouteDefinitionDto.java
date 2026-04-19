package com.g2rain.infra.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 网关路由表查询DTO
 * 表名: route_definition
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "网关路由提交 DTO")
public class RouteDefinitionDto extends BaseDto {

    /**
     * 路由名称
     */
    @Schema(description = "路由名称")
    private String name;

    /**
     * 终端主机
     */
    @Schema(description = "终端主机")
    private String endpointHost;

    /**
     * 终端路径
     */
    @Schema(description = "终端路径")
    private String endpointPath;

    /**
     * 转发路径
     */
    @Schema(description = "转发路径")
    private String context;

    /**
     * 请求路径
     */
    @Schema(description = "请求路径")
    private String path;

    /**
     * 请求方法
     */
    @Schema(description = "请求方法")
    private String method;

    /**
     * 请求头参
     */
    @Schema(description = "请求头参")
    private String headerParameters;

    /**
     * 内容类型
     */
    @Schema(description = "内容类型")
    private String contentType;

    /**
     * 业务说明
     */
    @Schema(description = "业务说明")
    private String description;
}