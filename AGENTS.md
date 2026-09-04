# g2rain-infra Agent Instructions

本文件是 AI Coding 在本项目中的执行入口。项目事实来源位于 `docs`，组织级公共规则位于中央 `g2rain` 仓库。

## 架构基线

- Profile：`java-domain-service 1.0.0`
- 固定引用：`architecture-v1.0.0`
- 当前状态：`planned`；完成 `docs/architecture/deviations.md` 的接入项并登记中央项目目录后才能改为 `adopted`

## 开始工作前

1. 读取 `docs/project.yaml`、中央 Profile 和 `docs/architecture/deviations.md`。
2. 按任务读取需求、架构、开发、安全或运维文档。
3. 核对源码、POM、配置、SQL、测试与 Git Diff，区分当前实现和目标规则。

## 强制边界

- 保持 `g2rain-infra-startup → g2rain-infra-biz → g2rain-infra-api`。
- API 模块发布查询和发号契约，不发布宽泛远程 CRUD；写入由 App 经 Gateway 调用 Biz Controller。
- Controller 只做协议适配，校验、状态变化、事务和事件发布位于 Service。
- Infra 数据是平台级公共元数据，不得虚构 `organId` 租户字段；写接口仍必须有明确认证、授权和审计。
- Snowflake Worker ID 必须保持唯一租约；租约无效时禁止继续发号。
- 号段更新必须保持数据库原子性、并发安全和业务标签唯一性。
- 错误消息事件与数据库写入的一致性风险必须显式处理，不得假定本地事务覆盖消息系统。
- 不记录或提交密码、Token、密钥、Redis/Nacos 凭证和私有地址。
- 修改 API、数据库、配置、模块、事件或发号算法时同步更新文档。

## 完成前

- 执行 `mvn clean verify`；无法执行时准确报告原因与风险。
- 检查 Markdown 链接、项目元数据、模块依赖、启动类、端口和镜像声明。
- 按 `docs/development/definition-of-done.md` 核对并报告未验证项。
- 保留用户已有修改，不提交构建产物、日志或无关文件。
