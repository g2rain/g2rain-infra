# g2rain-infra 文档

这里是 `g2rain-infra` 的项目级技术文档，也是 README、架构审核和 AI Coding 的事实来源。

项目按组织级 [`java-domain-service 1.0.0`](https://github.com/g2rain/g2rain/tree/architecture-v1.0.0/docs/architecture/profiles/java-domain-service) 进行接入准备，当前为 `planned`。中央 Profile 管理公共规则，本目录维护 Infra 的领域事实、运行约束和[架构差异](architecture/deviations.md)。

## 项目定位

Infra 管理平台公共字典、Locale、国际化消息及分布式 ID。当前实现不包含动态路由管理，Gateway 路由不属于本项目职责。

## 阅读路径

### 架构

- [架构总览](architecture/overview.md)
- [模块职责](architecture/modules.md)
- [依赖边界](architecture/dependencies.md)
- [核心运行流程](architecture/runtime-flows.md)
- [架构差异与接入项](architecture/deviations.md)

### 开发与交付

- [需求设计与验收模板](requirements/README.md)
- [本地开发](development/local-development.md)
- [代码规范](development/code-conventions.md)
- [API 设计规范](development/api-conventions.md)
- [数据库与数据模型](development/database-conventions.md)
- [测试策略](development/testing.md)
- [完成定义](development/definition-of-done.md)
- [CRUD 代码生成](development/code-generation.md)
- [架构决策记录](decisions/README.md)

### 安全与运维

- [安全边界](security/security-boundaries.md)
- [配置说明](operations/configuration.md)
- [构建与部署](operations/deployment.md)
- [可观测性](operations/observability.md)
- [故障排查](operations/troubleshooting.md)
- [社区、贡献与许可证](community.md)

修改模块、API、数据库、配置、事件、发号算法或部署方式时，应在同一提交同步相关文档。
