# G2rain Infra

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25-437291?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.1-586069?logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![Maven](https://img.shields.io/badge/build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](#参与贡献)

面向可扩展 SaaS 体系的**基础支撑服务**：提供字典、国际化、区域与语言、路由定义、分布式 ID（号段/雪花等）等通用能力，并与 Nacos、Redis 等基础设施集成，便于业务微服务统一接入。

本项目由 **[谷雨开源](https://g2rain.com)**（G2Rain）社区维护，采用 **Apache License 2.0** 开源协议发布。

---

## 目录

- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [模块结构](#模块结构)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [容器镜像](#容器镜像)
- [参与贡献](#参与贡献)
- [许可证](#许可证)

---

## 功能特性

- **字典与引用**：字典项、字典使用关系维护与查询。
- **国际化**：多语言文案及使用关系管理。
- **区域与语言**：语言区域（Locale）相关配置。
- **路由定义**：集中维护路由类元数据，供网关或业务侧消费。
- **Raindrop / ID 能力**：与号段、雪花等 ID 生成策略相关的支撑逻辑（具体行为以接口与实现为准）。
- **服务治理**：集成 **Nacos** 服务发现与动态配置；支持从 Nacos 导入 `g2rain-infra.yml` 等外部化配置。
- **消息与同步**：基于 **Spring Cloud Stream**（Redis Binder）输出到 `g2rain-syncer` 通道，配合缓存同步等场景。
- **可观测性**：Spring Boot **Actuator**（`health`、`info`）；集成 **springdoc-openapi** 便于联调与文档生成。

---

## 技术栈

| 类别 | 说明 |
|------|------|
| 运行时 | Java **25** |
| 框架 | **Spring Boot** 4.0.x、**Spring Cloud** 2025.1.x、**Spring Cloud Alibaba (Nacos)** |
| 持久化 | **MyBatis**、**MySQL** |
| 缓存 / 消息 | **Redis**、**Spring Cloud Stream**（Redis） |
| 其他 | **MapStruct**、**Lombok**、**springdoc-openapi** |

> 工程还依赖若干 `com.g2rain` 内部 Starter（如安全、Redis 封装、分页、缓存同步等）。若你在本地从零构建，需配置可解析这些构件的 **Maven 仓库**（或由社区提供 BOM / 公开坐标后再构建）。欢迎通过 Issue 反馈「仅开源本仓库时的最小可构建方案」需求。

---

## 模块结构

```
g2rain-infra/
├── g2rain-infra-api/      # 对外契约：API 接口、DTO/VO、枚举、错误码等
├── g2rain-infra-biz/      # 业务实现：Controller、Service、DAO、MyBatis Mapper
└── g2rain-infra-startup/  # 可执行应用：Spring Boot 入口、全局配置、OpenAPI 等
```

---

## 环境要求

- **JDK 25**（与 `maven.compiler.release` 一致）
- **Apache Maven 3.9+**（推荐）
- 运行期依赖：**MySQL**、**Redis**、**Nacos**（按 `application.yml` 与实际部署启用）

---

## 快速开始

### 1. 克隆仓库

```bash
git clone <你的仓库克隆地址>
cd g2rain-infra
```

### 2. 编译

```bash
mvn clean package -DskipTests
```

### 3. 运行

在具备数据库、Redis、Nacos 等依赖的前提下，启动可执行模块：

```bash
java -jar g2rain-infra-startup/target/g2rain-infra-startup-*.jar
```

或通过 Spring Boot 插件在 `g2rain-infra-startup` 模块目录下：

```bash
cd g2rain-infra-startup
mvn spring-boot:run
```

默认 HTTP 端口 **8080**（可通过环境变量 `SERVER_PORT` 覆盖）。激活的配置文件由 `SPRING_PROFILES_ACTIVE` 控制，默认引用 `dev` 等本地/开发配置。

### 4. API 文档

服务启动后，可通过 **springdoc-openapi** 提供的 UI 访问接口文档（路径以实际 springdoc 版本为准，一般为 `/swagger-ui.html` 或 `/swagger-ui/index.html`）。

---

## 配置说明

以下为常用环境变量与配置项（详见 `g2rain-infra-startup/src/main/resources/application.yml` 及 Nacos 中的 `g2rain-infra.yml`）。

| 变量 / 配置 | 说明 |
|-------------|------|
| `SERVER_PORT` | HTTP 端口，默认 `8080` |
| `SPRING_PROFILES_ACTIVE` | Spring Profile，默认 `dev` |
| `NACOS_SERVER_ADDR` | Nacos 地址，默认 `127.0.0.1:8848` |
| `SPRING_CLOUD_NACOS_*` | Nacos 鉴权、命名空间、分组等（建议使用环境变量或密钥管理注入，勿在生产使用弱默认口令） |

**安全提示**：请勿将生产环境数据库口令、Nacos 密码等敏感信息提交到公开仓库；优先使用 Nacos 加密配置、环境变量或密钥平台。

---

## 容器镜像

`g2rain-infra-startup` 模块集成了 **Jib**，可在配置好镜像仓库后执行构建（示例，具体以你环境为准）：

```bash
mvn -pl g2rain-infra-startup -am compile jib:build
# 或构建到本地 Docker daemon:
# mvn -pl g2rain-infra-startup -am compile jib:dockerBuild
```

基础镜像与镜像名见该模块 `pom.xml` 中 `jib-maven-plugin` 配置。

---

## 参与贡献

我们欢迎 Issue 与 Pull Request。建议流程：

1. **Fork** 本仓库并创建特性分支。
2. 保持提交信息清晰，改动与 Issue 或讨论主题对应。
3. 提交 **PR** 前在本地执行编译与必要测试：`mvn clean verify`（或项目约定的 CI 命令）。
4. 若改动涉及行为变更或新能力，请在 PR 描述中说明动机、使用方式及兼容性影响。

行为准则：请保持尊重、专业与建设性沟通；骚扰与歧视性内容不被接受。

---

## 许可证

本仓库根目录下的 [LICENSE](LICENSE) 文件适用 **Apache License, Version 2.0**。

```
Copyright © 2025 g2rain.com
```

使用本软件即表示你同意许可证条款。商标与品牌使用请以组织说明为准。

---

## 链接与社区

- **组织**：谷雨开源（G2Rain）
- **官网**：<https://www.g2rain.com>
- **问题反馈**：请在托管平台提交 **Issue**（请附上版本、JDK、复现步骤与日志片段）。

---

**说明**：仓库内曾误混入与 **g2rain-crafter** 插件相关的旧文档内容，现已更正为本服务的说明。若你需要 crafter 使用说明，请前往对应插件仓库或文档站点获取。
