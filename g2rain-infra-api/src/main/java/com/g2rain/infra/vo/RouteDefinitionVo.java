package com.g2rain.infra.vo;

import com.g2rain.common.json.AdminCompanyCondition;
import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 网关路由表返回VO
 * 关联表名: route_definition
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "网关路由 VO")
public class RouteDefinitionVo extends BaseVo {

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

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    private Boolean deleteFlag;
}
