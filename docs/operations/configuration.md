# 配置说明

入口为 `g2rain-infra-startup/src/main/resources/application.yml`，默认 profile 为 `dev`，并从 Nacos 导入 `g2rain-infra.yml`。

| 配置 | 默认行为 | 说明 |
| --- | --- | --- |
| `SERVER_PORT` | `8080` | HTTP 端口。 |
| `SPRING_PROFILES_ACTIVE` | `dev` | Spring Profile。 |
| `NACOS_SERVER_ADDR` | 本地地址 | 注册中心与配置中心。 |
| Nacos discovery/config username、password | 本地默认值 | 共享和生产环境必须覆盖。 |
| Nacos discovery/config namespace | `dev` | 环境隔离命名空间。 |
| `spring.datasource.*` | dev 或 Nacos | MySQL 数据源。 |
| `spring.data.redis.*` | Boot 默认或外部配置 | Worker 租约、缓存和 Stream Binder。 |
| `spring.cloud.stream.bindings.output.destination` | `g2rain-syncer` | 错误消息缓存同步通道。 |

仓库当前存在开发默认凭据，文档不复制具体值。部署前必须由环境变量、Secret 或受控配置中心覆盖，并确保 Redis 与数据库按环境隔离。
