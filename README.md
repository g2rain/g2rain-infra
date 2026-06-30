# g2rain-infra

## 1. 徽标与状态标识

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25-437291?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.1-586069?logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![Maven](https://img.shields.io/badge/build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)

## 2. 项目简介

`g2rain-infra` 是 G2rain 平台中的基础元数据与通用支撑服务，负责提供字典、国际化、语言地区配置、全局 ID 分配与基础同步等平台公共能力。

## 3. 平台定位

在 G2rain“企业级 AI 原生开源 SaaS 平台”体系中，`g2rain-infra` 位于平台核心服务层，是平台公共元数据与基础运行支撑的重要承载服务。

它主要服务以下场景：
- 为主壳、子应用与平台控制台提供统一字典、国际化与语言地区基础数据
- 为平台后端服务提供全局 ID 分配与运行时节点协调能力
- 为平台统一缓存同步与配置治理提供消息通道与基础设施接入能力
- 为多模块工程化演进提供稳定的契约层、实现层与启动层结构

它与 `g2rain-basis`、`g2rain-iam`、`g2rain-main-shell`、`g2rain-gateway-webmvc`、`g2rain-gateway-webflux` 等仓库协同，共同构成平台治理、身份、安全与公共基础能力底座。

## 4. 核心能力

本章回答“这个仓库在平台里提供什么能力、解决什么问题”。

- 字典与用途治理能力：解决平台各业务域如何共享稳定字典、选项集与树形字典的问题，通过 `DictionaryUsage*`、`DictionaryItem*` 相关接口与实现沉淀统一字典治理能力。
- 国际化消息与页面文案能力：解决平台多语言文案如何统一维护、按标签与地区拉取以及变更同步的问题，通过 `I18nMessage*` 能力沉淀国际化治理入口。
- 语言地区标准化能力：解决语言与地区配置如何统一校验、统一编码与统一输出的问题，通过 `LocaleSetting*` 能力为前端和服务侧提供稳定地区语言数据。
- 全局 ID 分配能力：解决平台多服务场景下业务主键与全局唯一 ID 如何统一生成的问题，通过 `G2rainRaindrop*`、`SegmentKeysmith`、`SnowflakeKeysmith` 提供两类分配策略。
- 运行时节点协调能力：解决 Snowflake 模式下多实例 `workerId` 如何安全抢占、续租与失效恢复的问题，通过 `RedisWorkerIdManager` 与 Redis Lua 脚本完成分布式协调。
- 基础同步与工程化支撑能力：解决公共元数据变更如何广播、工程如何持续生成和演进的问题，通过 Stream 输出通道、`codegen.properties` 与 `g2rain-crafter` 提供同步与工程化支撑。

## 5. 技术栈

- 语言与运行时：`Java 25`
- 后端框架：`Spring Boot 4.0.5`、`Spring Cloud 2025.1.1`、`Spring Cloud Alibaba Nacos`
- 持久化：`MyBatis`、`MySQL`
- 缓存与协调：`Redis`
- 消息与同步：`Spring Cloud Stream`（Redis Binder）
- 对象转换：`MapStruct`
- 文档与治理：`springdoc-openapi`
- 工程化：`g2rain-crafter`
- 构建与交付：`Maven`、`Jib`、`Dockerfile`、`build.sh`

## 6. 快速开始

### 环境要求

- `JDK 25`
- `Maven 3.9+`
- 可用的 `MySQL`
- 可用的 `Redis`
- 可用的 `Nacos`

### 关键配置

当前仓库的关键运行配置主要来自 `g2rain-infra-startup/src/main/resources/application.yml` 与 Nacos 配置中心。

| 变量名 | 说明 | 典型用途 |
| --- | --- | --- |
| `SERVER_PORT` | 服务端口 | 默认 `8080` |
| `SPRING_PROFILES_ACTIVE` | 启动环境 | 区分 `dev` 等 profile |
| `NACOS_SERVER_ADDR` | Nacos 地址 | 服务发现与配置中心 |
| `SPRING_CLOUD_NACOS_DISCOVERY_*` | 注册中心认证与命名空间 | 服务注册 |
| `SPRING_CLOUD_NACOS_CONFIG_*` | 配置中心认证与命名空间 | 外部配置拉取 |

建议：
- 生产环境优先通过 Nacos 或安全配置中心维护数据库、Redis、Nacos 凭据。
- `scripts/g2rain-infra.sql` 应作为初始化脚本统一纳入部署流程。
- `codegen.properties` 与数据库表结构应保持一致，避免工程化输出与实际实现脱节。

### 本地构建

```bash
mvn clean install -DskipTests
```

### 本地运行

```bash
mvn -pl g2rain-infra-startup -am spring-boot:run
```

或：

```bash
java -jar g2rain-infra-startup/target/g2rain-infra-startup-1.0.0.jar
```

### 镜像构建

```bash
./build.sh
./build.sh latest
```

或：

```bash
mvn -pl g2rain-infra-startup -am compile jib:dockerBuild
```

## 7. 项目结构

本章回答“代码与模块是如何组织的、排查和扩展时应该先看哪里”。

```text
g2rain-infra/
├── codegen.properties
├── build.sh
├── scripts/
│   └── g2rain-infra.sql
├── g2rain-infra-api/
├── g2rain-infra-biz/
└── g2rain-infra-startup/
```

### 结构说明

- `g2rain-infra-api/`：承载对外 API 契约、DTO、VO、枚举与错误码，是调用方对接的稳定接口层。
- `g2rain-infra-biz/`：承载控制器、服务、DAO、MapStruct 转换器、运行时组件与业务实现，是仓库核心领域逻辑所在。
- `g2rain-infra-startup/`：承载 Spring Boot 启动入口、Nacos 接入、Stream 配置、虚拟线程、参数解析与运行时装配。
- `scripts/g2rain-infra.sql`：承载数据库初始化结构与基础数据，是部署与本地落库的重要入口。
- `codegen.properties`：承载代码生成配置，是多模块工程化演进的重要支撑文件。
- `build.sh`：承载仓库默认镜像交付入口，会先整仓构建再进入启动模块执行 Jib。

### 代码查阅指引

- 查看字典能力时，优先看 `DictionaryUsage*`、`DictionaryItem*` 与对应 `ServiceImpl`。
- 查看国际化能力时，优先看 `I18nMessage*` 与同步相关实现。
- 查看语言地区能力时，优先看 `LocaleSetting*`。
- 查看全局 ID 能力时，优先看 `G2rainRaindrop*`、`SegmentKeysmith`、`SnowflakeKeysmith`。
- 查看 Snowflake 运行协调时，优先看 `RedisWorkerIdManager`。
- 查看运行期装配与配置接入时，优先看 `g2rain-infra-startup` 下的 `Application`、`ArgumentResolverConfig`、`VirtualThreadConfigurer` 与 `application.yml`。

## 8. 核心业务流程

本章回答“这些能力在运行时是如何串起来工作的”。

#### 1. 标准元数据服务主线

- 外部调用方先通过 `g2rain-infra-api` 契约访问平台基础能力接口。
- `controller` 实现统一 HTTP 入口，`service/impl` 承接校验、组装与业务逻辑。
- 最终由 `dao + mybatis mapper` 落到 MySQL，输出稳定元数据或管理结果。
- 这一主线保证了 API 契约、领域实现与运行时装配三层边界长期稳定。

#### 2. 字典能力主线

- `DictionaryItemController` 提供列表、分页、树形结构与本地化选项等接口。
- `DictionaryItemServiceImpl` 会校验用途编码、唯一性与父子关系，再补齐父节点展示信息。
- 树形接口会按 `parentId` 在内存中组装并排序，形成统一树形输出。
- 这一主线解决的是平台多业务域共享字典与选项集的统一治理问题。

#### 3. 国际化与同步主线

- `I18nMessageController` 提供国际化消息维护、标签字典与页面文案拉取能力。
- `I18nMessageServiceImpl` 校验用途、标签、语言地区与扩展字段，再完成保存或查询。
- 变更时通过 Stream 输出通道把同步事件发送到 `g2rain-syncer`。
- 这一主线解决的是多服务之间国际化消息一致性与变更广播问题。

#### 4. 语言地区主线

- `LocaleSettingServiceImpl` 启动时先构建 JVM 可用语言地区字典。
- 保存配置时校验 `code` 合法性，并拆解为 `languageCode` 与 `regionCode`。
- 最终对外输出 `localeDict`、`languageCountries`、`code2name` 等支撑数据。
- 这一主线解决的是平台语言地区配置标准化与复用问题。

#### 5. 全局 ID 分配主线

- `G2rainRaindropServiceImpl` 维护业务标签与分配策略入口。
- 系统根据 `KeysmithType` 路由到 `SegmentKeysmith` 或 `SnowflakeKeysmith`。
- `SegmentKeysmith` 负责数据库号段预分配、双缓冲切换与高并发连续取号。
- `SnowflakeKeysmith` 负责时间戳、`workerId` 与序列号组合生成全局唯一 ID。
- 这一主线解决的是平台不同场景下统一 ID 分配策略的问题。

#### 6. Snowflake 节点协调主线

- `SnowflakeKeysmith` 启动时先通过 `RedisWorkerIdManager` 抢占 `workerId`。
- `RedisWorkerIdManager` 基于 Redis Lua 脚本完成原子分配、续租与释放。
- 若续租连续失败超过阈值，系统会标记当前 `workerId` 失效并尝试重新申请。
- 这一主线解决的是多实例 Snowflake 场景下节点标识冲突与失效恢复问题。

## 9. 常用命令

```bash
mvn clean install
mvn -pl g2rain-infra-startup -am spring-boot:run
mvn -pl g2rain-infra-startup -am compile jib:dockerBuild
./build.sh
./build.sh latest
```

## 10. 质量与测试

- 当前扫描未发现 `src/test/java` 测试源码。
- 建议后续优先补齐字典树组装、国际化同步、Segment 号段切换、Snowflake 时钟回拨与续租失效等关键链路测试。
- 当前仓库工程结构较稳定，但运行机制型代码较多，回归测试价值较高。

## 11. 相关仓库

- `g2rain-basis`：平台应用、资源、角色与权限治理底座
- `g2rain-iam`：统一身份认证与令牌服务
- `g2rain-main-shell`：主壳与统一交互入口
- `g2rain-gateway-webmvc`：网关与接入安全协同实现之一
- `g2rain-gateway-webflux`：网关与接入安全协同实现之一

## 12. 使用建议

- 适合作为平台统一基础元数据与基础支撑服务独立部署，而不是拆散到业务服务内各自维护。
- 适合为控制台、网关和业务服务统一提供字典、国际化、语言地区与 ID 分配能力。
- 生产环境请将 MySQL、Redis、Nacos 凭据统一托管在安全配置中心。
- 对于 `scripts/g2rain-infra.sql` 和 `codegen.properties`，建议在交付与演进流程中明确纳入版本管理与变更评审。

## 13. 贡献指南

欢迎通过文档改进、Issue 反馈、测试补充、代码优化、功能增强等形式参与贡献。

建议流程：
1. Fork 本仓库
2. 创建特性分支
3. 提交修改
4. 推送分支
5. 提交 Pull Request

提交前请尽量确保：
- 遵循现有技术栈与代码规范
- 更新相关文档
- 如涉及关键流程，补充必要测试

## 14. 许可证

本项目基于 [Apache 2.0许可证](LICENSE) 开源。

## 15. 联系我们

- **站点**: https://www.g2rain.com/
- **Issues**: [GitHub Issues](https://github.com/g2rain/g2rain/issues)
- **讨论**: [GitHub Discussions](https://github.com/g2rain/g2rain/discussions)
- **邮箱**: g2rain_developer@163.com

## 16. 致谢

感谢所有为这个项目做出贡献的开发者们。

如果这个项目对您有帮助，欢迎 Star 支持。
