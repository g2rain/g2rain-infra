# 架构差异与接入项

项目当前按 `java-domain-service 1.0.0` 准备接入，但尚未登记中央项目目录，因此状态为 `planned`。

## INF-001：测试覆盖为空

`2026-09-04` 执行 `mvn clean verify` 通过，四个 Reactor 项目构建成功，但三个模块均显示 `No tests to run`。该结果不能证明 Redis Worker 租约、时钟回拨、号段并发、双缓冲切换、数据库约束、事件一致性、Controller 权限或 Startup 组装正确。

## INF-002：字典业务唯一性只由应用查询保护

`dictionary_usage` 和 `dictionary_item` 使用逻辑删除，但 SQL 初始化脚本没有为用途编码、用途内字典项编码建立数据库唯一约束。Service 的“先查再写”在并发下不能保证唯一，应明确删除后是否允许重建，并使用符合中央 Profile 的有效行函数索引。

## INF-003：错误消息写入与事件发布一致性

`I18nMessageServiceImpl` 在本地事务内写数据库并直接发布缓存事件，但没有可见 Outbox、发布确认补偿或集成测试证据。发布失败、事务回滚或消费者不可用时可能造成数据库与缓存状态不一致。

## INF-004：开发凭证进入版本库

`application.yml`、`application-dev.yml` 和 `codegen.properties` 包含本地默认 Nacos/数据库凭证。文档不会复制具体值；接入基线前应迁移到环境变量、Secret 或私有本地覆盖，并在凭证曾被复用时轮换。

## INF-005：写接口访问控制证据不足

五类管理 Controller 提供保存和删除接口，但当前项目源码中没有可见的细粒度权限注解或对应安全测试。Aegis、Gateway 或部署策略可能提供外层保护，正式接入前仍需验证调用链、角色授权、审计和绕过 Gateway 的拒绝行为。

## INF-006：代码生成配置包含未实现表

`codegen.properties` 仍列出 `route_definition` 和 `i18n_message_usage`，当前源码及初始化 SQL 没有对应领域实现。运行 Crafter 前必须收敛表清单，避免误生成或覆盖；动态路由不得写入正式能力说明。
