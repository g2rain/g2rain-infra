# API 设计规范

## 契约边界

`g2rain-infra-api` 当前发布五组 GET 契约：字典用途、字典项、Locale、国际化消息及 `g2rain_raindrop` 查询/发号。保存和删除接口位于 Biz Controller，不进入共享 API 模块，符合查询优先原则。

- 返回值统一使用 `Result<T>`，分页使用 `PageData<T>`。
- 路径沿用 snake_case，如 `/dictionary_item/localized_options`。
- App 写请求携带 IAM Token 经 Gateway 调用。
- 服务间调用默认只复用查询和发号契约；新增同步写契约需要需求、权限、事务、幂等和兼容设计，并登记架构例外。
- Controller 只做参数绑定、验证和结果包装。

发号接口是有状态查询：Snowflake 依赖 Redis Worker 租约，业务号段会原子推进数据库 `max_id`。调用方只能在明确可安全重试时重试，不能假定超时等于未分配。
