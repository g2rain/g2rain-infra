# 模块职责

## `g2rain-infra-api`

发布可复用查询和发号契约，包含 `api`、`dto`、`vo` 与 `enums`。当前接口均为 GET 查询或 ID 申请，不发布保存、删除等宽泛远程 CRUD。

## `g2rain-infra-biz`

| 包 | 职责 |
| --- | --- |
| `controller` | 实现 API 查询，并提供管理端保存/删除入口。 |
| `service` / `service.impl` | 领域校验、状态变化、事件和发号算法。 |
| `dao` / `dao.po` | MyBatis 持久化。 |
| `components` | Redis Worker ID 租约管理。 |
| `model` | 号段缓冲与段状态。 |
| `converter` | DTO、VO 与 PO 转换。 |

## `g2rain-infra-startup`

提供 `com.g2rain.infra.Application`，组装 Biz、Web、Actuator、OpenAPI 和运行配置，并通过 Spring Boot Maven Plugin/Jib 生成可运行产物。

## 根工程

根 POM 管理 Java 25、依赖版本、三个模块、Flatten 插件和 Crafter；`codegen.properties` 仅用于代码生成。
