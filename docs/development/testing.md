# 测试策略

当前仓库没有测试源码。新增测试优先级如下：

| 层级 | 最低覆盖 |
| --- | --- |
| Snowflake | Worker 获取/续租/释放、租约失效、时钟回拨、序列溢出和多节点不重复。 |
| Segment | 首次加载、并发取号、预取、切换、动态步长、未知 bizTag 和数据库失败。 |
| Service | 字典唯一性、树结构、Locale 校验、JSON 扩展字段、错误消息事件。 |
| DAO/数据库 | 原子推进、乐观锁、逻辑删除、唯一索引和重复写竞争。 |
| API/安全 | 参数、完整路径、认证、授权、越权和发号超时语义。 |
| Startup/集成 | Redis/MySQL/Nacos、Mapper、事件 Binder、Bean 和健康检查。 |

```bash
mvn clean verify
```

Mock 单元测试不能证明 Redis Lua、MySQL 并发约束或数据库—事件一致性，关键发号路径需要真实依赖的集成与并发测试。
