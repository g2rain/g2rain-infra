# 可观测性

Actuator 当前暴露 `health` 和 `info`，生产环境应限制访问范围。

建议观察：

- Snowflake Worker 获取、续租、失效、重新申请和释放次数。
- 时钟回拨拒绝、ID 申请延迟与错误率。
- 号段首次加载、预取、切换、步长、剩余量、数据库更新失败和未知 bizTag。
- 字典、Locale、国际化写入失败及错误消息事件发布失败/积压。
- MySQL、Redis、Nacos、线程池和服务注册健康。

日志携带 trace/request 与安全的业务标签，不记录凭据、Token 或完整敏感文案载荷。
