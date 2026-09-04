<p align="center">
  <img src="https://github.com/g2rain.png" alt="G2Rain" width="180" />
</p>

# g2rain-infra

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25-437291?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.1-586069?logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![Maven](https://img.shields.io/badge/build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)

> 下一代AI软件开发范式，AI原生Agent平台，开源的企业级SaaS底座。

`g2rain-infra` 是 g2rain 的平台基础元数据与分布式发号服务，统一维护字典、地域语言和国际化消息，并提供 Redis Worker 租约 Snowflake 与 MySQL 号段两种 ID 分配能力。

[官网](https://www.g2rain.com) · [完整文档](docs/index.md) · [中央架构 Profile](https://github.com/g2rain/g2rain/tree/architecture-v1.0.0/docs/architecture/profiles/java-domain-service) · [架构说明](docs/architecture/overview.md) · [代码规范](docs/development/code-conventions.md) · [Issues](https://github.com/g2rain/g2rain/issues) · [Discussions](https://github.com/g2rain/g2rain/discussions)

## 目录

- 项目简介
- 平台定位
- 业务域说明
- 功能概览
- 技术栈
- 环境要求
- 快速开始
- 构建与镜像
- 配置与安全
- 接口示例
- 模块说明
- 职责边界
- 主要 HTTP 路径
- 测试现状
- 项目文档
- 关联仓库
- 参与贡献
- 许可证
- 联系我们
- 致谢

## 项目简介

Infra 管理平台公共字典、Locale、国际化消息及分布式 ID，为管理端、Gateway 和其他平台/业务服务提供稳定的基础数据与发号能力。当前源码不包含动态路由表、Controller 或 Service；`route_definition` 仅存在于代码生成配置，不能视为已交付能力。

## 平台定位

该仓库位于 g2rain 后端基础服务层。管理端 App 携带 IAM Token 经 Gateway 维护公共元数据；其他服务复用 `g2rain-infra-api` 查询字典、国际化资源或申请 ID。

项目正按 [`java-domain-service 1.0.0`](https://github.com/g2rain/g2rain/tree/architecture-v1.0.0/docs/architecture/profiles/java-domain-service) 准备接入，当前状态为 `planned`；已知差异见[架构差异与接入项](docs/architecture/deviations.md)。

## 业务域说明

该仓库聚焦于 `平台基础设施管理`。

核心对象包括：字典用途、字典项、Locale 设置、国际化消息、Snowflake Worker 租约和业务号段。

## 功能概览

| 能力 | 说明 |
| --- | --- |
| 字典管理 | 维护字典用途、字典项及其本地化选项。 |
| 国际化管理 | 维护地域语言配置、UI 文案和错误消息；错误消息变化会发布缓存同步事件。 |
| Snowflake 发号 | 通过 Redis Lua 分配 Worker ID，使用租约续期和时钟回拨保护生成 64 位 ID。 |
| 业务号段发号 | 通过 MySQL 原子推进号段，使用双缓冲预取和动态步长分配业务 ID。 |

## 技术栈

| 类别 | 说明 |
| --- | --- |
| 运行时 | Java 25、Spring Boot 4.0.5、Spring Cloud 2025.1.1 |
| 安全与令牌 | g2rain-starter-aegis-core |
| 数据访问 | MyBatis 4.0.1、MySQL Connector/J 9.6.0、MapStruct 1.6.3 |
| 基础设施 | MySQL、Redis、Nacos、Spring Cloud Stream Redis Binder |
| 平台组件 | g2rain-common、Aegis、Cache Sync、MyBatis Pagination、Spring Doc |

## 环境要求

- JDK 25+
- Maven 3.9+
- MySQL 8.0.13+
- Redis
- Nacos

## 快速开始

| 步骤 | 命令或位置 | 说明 |
| --- | --- | --- |
| 初始化数据库 | `scripts/g2rain-infra.sql` | 创建五张当前实现使用的基础数据表。 |
| 调整配置 | `g2rain-infra-startup/src/main/resources/application.yml` | 通过环境变量或 Nacos 提供数据库、Redis 和注册中心配置。 |
| 构建验证 | `mvn clean verify` | 编译三个模块并运行现有测试。 |
| 本地启动 | `mvn -pl g2rain-infra-startup -am spring-boot:run` | 启动 Startup 模块，默认端口 `8080`。 |

版本号以项目构建配置为准，当前识别为 `1.0.0`。

## 构建与镜像

| 目标 | 命令 | 产物 | 说明 |
| --- | --- | --- | --- |
| 完整验证 | `mvn clean verify` | 三模块构建结果 | 当前没有测试源码，结果主要证明编译和打包。 |
| 可执行 Jar | `mvn clean package` | `g2rain-infra-startup/target/g2rain-infra-startup-1.0.0.jar` | 由 Startup 模块生成 Spring Boot Jar。 |
| 本地运行 | `mvn -pl g2rain-infra-startup -am spring-boot:run` | 本地 Spring Boot 进程 | 使用当前 profile 启动服务。 |
| 本地镜像 | `./build.sh <tag>` | `g2rain/g2rain-infra:<tag>` | 通过 Jib 构建；脚本自身跳过测试，发布前需先验证。 |

## 配置与安全

- 默认端口为 `8080`、profile 为 `dev`，运行配置位于 `g2rain-infra-startup/src/main/resources/application.yml`。
- MySQL、Redis 和 Nacos 配置应由环境变量、Secret 或受控配置中心注入。
- 仓库当前仍包含本地开发默认凭据，不得用于共享或生产环境。
- 保存、删除和号段配置属于高影响操作，应携带 IAM Token 经 Gateway 调用并保留授权与审计证据。

详见[配置说明](docs/operations/configuration.md)与[安全边界](docs/security/security-boundaries.md)。

## 接口示例

完整接口由 OpenAPI 和 `g2rain-infra-api` 描述。公开请求应携带有效凭证并经 Gateway 访问，例如：

```bash
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/g2rain_raindrop/business?bizTag=ORDER"
```

发号请求可能已经推进序列后才发生网络超时，调用方不得以“必须连续”为前提盲目重试。

## 模块说明

| 模块 | 职责说明 | 代码线索 |
| --- | --- | --- |
| g2rain-infra-api | 发布字典、Locale、国际化消息和发号查询契约及 DTO/VO。 | `api`、`dto`、`vo`、`enums` |
| g2rain-infra-biz | 实现管理入口、领域校验、MyBatis、错误消息事件和两类发号器。 | `controller`、`service`、`dao`、`components` |
| g2rain-infra-startup | 组装 Spring Boot、Actuator、OpenAPI、运行配置和镜像。 | `Application`、`application.yml`、Jib |

## 职责边界

该仓库主要负责：

- 平台公共字典、Locale 与国际化消息。
- Redis Worker 租约 Snowflake 和 MySQL 业务号段发号。
- 错误消息变化的缓存同步事件。

该仓库默认不负责：

- Gateway 动态路由、统一入口鉴权和请求转发。
- IAM 登录、OAuth/OIDC、Token 和 Session。
- 具体业务域数据、前端页面和部署编排。

## 主要 HTTP 路径

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/dictionary_item/tree` | 查询树形字典。 |
| GET | `/dictionary_item/localized_options` | 按 Locale 查询字典选项。 |
| POST | `/dictionary_item/save` | 新增或更新字典项。 |
| GET | `/locale_setting/locale_dict` | 查询 JDK 支持的 Locale 编码。 |
| GET | `/locale_setting/code_name_map` | 查询 Locale 编码名称映射。 |
| GET | `/i18n_message/locale` | 按标签和 Locale 查询 UI 文案。 |
| POST | `/i18n_message/save` | 保存国际化消息并按类型发布错误消息事件。 |
| GET | `/g2rain_raindrop/snowflake` | 申请 Snowflake ID。 |
| GET | `/g2rain_raindrop/business?bizTag=<tag>` | 按业务标签申请号段 ID。 |

## 测试现状

仓库当前没有 `src/test` 测试源码。构建通过只能证明编译、打包和 Maven 生命周期成功，不能证明 Redis Worker 租约、时钟回拨、号段并发、数据库唯一性或消息一致性。详见[测试策略](docs/development/testing.md)。

## 项目文档

| 主题 | 文档 |
| --- | --- |
| 文档首页 | [docs/index.md](docs/index.md) |
| 架构与模块 | [架构总览](docs/architecture/overview.md) · [模块职责](docs/architecture/modules.md) · [依赖边界](docs/architecture/dependencies.md) · [运行流程](docs/architecture/runtime-flows.md) |
| 中央基线与差异 | [Java Domain Service 1.0.0](https://github.com/g2rain/g2rain/tree/architecture-v1.0.0/docs/architecture/profiles/java-domain-service) · [接入项](docs/architecture/deviations.md) |
| 开发规范 | [代码](docs/development/code-conventions.md) · [API](docs/development/api-conventions.md) · [数据库](docs/development/database-conventions.md) · [测试](docs/development/testing.md) · [完成定义](docs/development/definition-of-done.md) |
| 安全与运维 | [安全](docs/security/security-boundaries.md) · [配置](docs/operations/configuration.md) · [部署](docs/operations/deployment.md) · [可观测性](docs/operations/observability.md) · [故障排查](docs/operations/troubleshooting.md) |

## 关联仓库

| 仓库 | 协作关系 |
| --- | --- |
| [g2rain](https://github.com/g2rain/g2rain) | 维护组织级架构 Profile、ADR 和项目目录。 |
| [g2rain-common](https://github.com/g2rain/g2rain-common) | 提供统一结果、异常、ID 接口、事件模型和公共工具。 |
| [g2rain-spring-boot-starter](https://github.com/g2rain/g2rain-spring-boot-starter) | 提供 Aegis、Redis、缓存同步、Stream 和 OpenAPI 集成。 |
| [g2rain-gateway-webflux](https://github.com/g2rain/g2rain-gateway-webflux) | 承接外部统一入口并访问 Infra 能力。 |
| [g2rain-infra-app](https://github.com/g2rain/g2rain-infra-app) | 提供基础元数据与发号配置管理页面。 |

## 参与贡献

我们欢迎所有形式的贡献：Issue 反馈、文档改进、功能建议与代码提交。

推荐流程：

1. Fork 本仓库。
2. 创建特性分支：`git checkout -b feature/your-feature-name`。
3. 提交更改：`git commit -m "Add some feature"`。
4. 推送分支：`git push origin feature/your-feature-name`。
5. 提交 Pull Request。

代码贡献前请尽量补充必要的测试和文档，并确保构建、测试与静态检查通过。

## 许可证

本项目基于 [Apache 2.0许可证](https://github.com/g2rain/g2rain-common/blob/main/LICENSE) 开源。

## 联系我们

- Issues: [GitHub Issues](https://github.com/g2rain/g2rain/issues)
- 讨论: [GitHub Discussions](https://github.com/g2rain/g2rain/discussions)
- 邮箱: g2rain_developer@163.com

## 致谢

感谢所有为 g2rain 项目提交 Issue、代码、文档、建议和使用反馈的开发者们！
