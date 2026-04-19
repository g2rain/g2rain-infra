package com.g2rain.infra.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 网关路由表返回Po
 * 关联表名: route_definition
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RouteDefinitionPo extends BasePo {

    /**
     * 路由名称
     */
    private String name;

    /**
     * 终端主机
     */
    private String endpointHost;

    /**
     * 终端路径
     */
    private String endpointPath;

    /**
     * 转发路径
     */
    private String context;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求头参
     */
    private String headerParameters;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 业务说明
     */
    private String description;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}