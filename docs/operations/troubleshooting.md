# 故障排查

## 服务启动失败

- Snowflake 初始化必须从 Redis 取得 Worker ID；检查 Redis 连接、Lua 权限和 Worker 池。
- 检查 Nacos 地址、namespace、group 和 `g2rain-infra.yml`。
- 检查 MySQL 数据源及 `scripts/g2rain-infra.sql` 是否已执行。

## Snowflake 发号失败

- `WORKER_ID_INVALID`：检查连续续租失败和 Redis 网络。
- 时钟回拨错误：检查宿主机时间同步，不要绕过保护继续发号。
- Worker 池耗尽：检查实例数量、过期租约回收和异常退出。

## 业务号段失败

- 确认 `bizTag` 存在且有效；默认标签是 `COMMON`。
- 检查 `max_id` 原子更新、版本条件和数据库锁等待。
- Segment 未就绪时检查异步预取异常和数据库延迟。

## 国际化缓存未更新

- 仅 `ERROR_CODE` 类型变更会发布 `ERROR_MSG` 事件。
- 检查 `g2rain-syncer` destination、Redis Binder、消费者和事件积压。
- 数据库已成功但事件失败时按对账/补偿流程恢复，不能反复无条件写入。

## 代码生成出现意外文件

检查 `codegen.properties` 的表清单与 `tables.overwrite`。当前两个残留表名没有对应实现，生成前必须移除或完成需求设计。
