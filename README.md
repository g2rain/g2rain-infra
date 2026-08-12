<p align="center">
  <img src="https://github.com/g2rain.png" alt="G2Rain" width="180" />
</p>

# g2rain-infra

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25-437291?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.1-586069?logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![Maven](https://img.shields.io/badge/build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)

下一代AI软件开发范式，AI原生Agent平台，开源的企业级SaaS底座。

平台基础设施元数据服务，提供字典、国际化、地区语言、动态路由与分布式发号器等通用能力；为网关、平台应用与业务服务提供统一基础配置来源

[官网](https://www.g2rain.com) · [Issues](https://github.com/g2rain/g2rain/issues) · [Discussions](https://github.com/g2rain/g2rain/discussions)

## 目录

- 项目简介
- 平台定位
- 业务域说明
- 功能概览
- 技术栈
- 环境要求
- 快速开始
- 构建与镜像
- 模块说明
- 职责边界
- 主要 HTTP 路径
- 关联仓库
- 参与贡献
- 许可证
- 联系我们
- 致谢

## 项目简介

平台基础设施元数据服务，提供字典、国际化、地区语言、动态路由与分布式发号器等通用能力；为网关、平台应用与业务服务提供统一基础配置来源

## 平台定位

该仓库位于 g2rain 后端平台链路中，承担“后端基础服务”的角色。

## 业务域说明

该仓库聚焦于 `平台基础设施管理`。

核心对象包括：
- 应用

## 功能概览

| 能力 | 说明 |
| --- | --- |
| 字典管理 | 维护字典用途、字典项及其本地化选项。 |
| 国际化管理 | 维护地区语言配置与多语言消息资源。 |
| 动态路由元数据 | 集中维护网关可消费的服务路由定义。 |
| 分布式发号器 | 提供平台统一编号与分布式 ID 生成能力。 |

## 技术栈

| 类别 | 说明 |
| --- | --- |
| 运行时 | Java 25、Spring Boot 4.0.5、Spring Cloud 2025.1.1 |
| 安全与令牌 | g2rain-starter-aegis-core |
| 基础设施 | Redis、Nacos |
| 其他 | Lombok |

## 环境要求

- JDK 25+
- Maven 3.9+
- Redis
- Nacos

## 快速开始

| 步骤 | 命令或位置 | 说明 |
| --- | --- | --- |
| 准备运行环境 | JDK 25+、Maven 3.9+、Redis、Nacos | 后端服务启动前需要准备 Java 构建环境和平台依赖的基础设施。 |
| 调整配置 | `src/main/resources/application.yml` | 按需设置 SERVER_PORT、SPRING_PROFILES_ACTIVE、NACOS_SERVER_ADDR 等环境变量。 |
| 构建项目 | `mvn clean package` | 执行 Maven 构建并生成可执行 Jar。 |
| 本地启动 | `mvn spring-boot:run` | 以当前 profile 启动服务，默认端口以 application.yml 中的 SERVER_PORT 为准。 |

版本号以项目构建配置为准，当前识别为 `1.0.0`。

## 构建与镜像

| 目标 | 命令 | 产物 | 说明 |
| --- | --- | --- | --- |
| 可执行 Jar | `mvn clean package` | `g2rain-infra-1.0.0.jar` | 执行 Maven 标准构建，生成服务可执行产物。 |
| 本地运行 | `mvn spring-boot:run` | 本地 Spring Boot 进程 | 使用当前 profile 启动服务，便于本地联调。 |
| 构建脚本 | `./build.sh` | 脚本定义的构建结果 | 仓库提供 build.sh，可承载组织内约定的镜像或发布流程。 |

## 模块说明

| 模块 | 职责说明 | 代码线索 |
| --- | --- | --- |
| g2rain-infra-api | 定义字典、国际化、路由与发号器 API 契约。 | g2rain-infra-api |
| g2rain-infra-biz | 实现平台基础设施元数据管理业务。 | g2rain-infra-biz |
| g2rain-infra-startup | 提供 Spring Boot 启动入口与运行配置。 | g2rain-infra-startup |

## 职责边界

该仓库主要负责：
- 负责对应平台基础领域的 API、业务规则、数据持久化与运行时服务
- 负责向网关、IAM、平台应用或业务服务提供可复用的基础能力

该仓库默认不负责：
- 不负责具体业务域的产品流程和业务前端实现
- 不替代网关统一入口、IAM 认证协议或部署编排职责

## 主要 HTTP 路径

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| DELETE | /dictionary_item/{id} | 对外暴露的服务接口 |
| DELETE | /dictionary_usage/{id} | 对外暴露的服务接口 |
| DELETE | /g2rain_raindrop/{id} | 对外暴露的服务接口 |
| DELETE | /i18n_message/{id} | 对外暴露的服务接口 |
| DELETE | /locale_setting/{id} | 对外暴露的服务接口 |
| GET | /abc | 对外暴露的服务接口 |
| GET | /business | 对外暴露的服务接口 |
| GET | /g2rain_raindrop/biz_tag_dict | 对外暴露的服务接口 |
| GET | /i18n_message/i18n_message_usages | 对外暴露的服务接口 |
| GET | /i18n_message/locale | 对外暴露的服务接口 |
| GET | /i18n_message/tag_dict | 对外暴露的服务接口 |
| GET | /list | 对外暴露的服务接口 |
| GET | /locale_setting/code_name_map | 对外暴露的服务接口 |
| GET | /locale_setting/get_language_countries | 对外暴露的服务接口 |
| GET | /locale_setting/locale_dict | 对外暴露的服务接口 |
| GET | /localized_options | 对外暴露的服务接口 |
| GET | /page | 对外暴露的服务接口 |
| GET | /snowflake | 对外暴露的服务接口 |
| GET | /tree | 对外暴露的服务接口 |
| POST | /dictionary_item/save | 对外暴露的服务接口 |
| POST | /dictionary_usage/save | 对外暴露的服务接口 |
| POST | /g2rain_raindrop/save | 对外暴露的服务接口 |
| POST | /i18n_message/save | 对外暴露的服务接口 |
| POST | /locale_setting/save | 对外暴露的服务接口 |

## 关联仓库

| 仓库 | 协作关系 |
| --- | --- |
| g2rain-common | 复用平台公共规范、通用模型、工具能力或基础依赖约束。 |

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
