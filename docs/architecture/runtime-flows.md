# 核心运行流程

## Snowflake 发号

```text
服务启动
→ Redis Lua 原子初始化/弹出 0..1023 Worker ID
→ 建立 40 秒租约，每 10 秒续租
→ GET /g2rain_raindrop/snowflake
→ 校验租约有效、处理时钟回拨与毫秒内序列
→ 返回 64 位 ID
→ 服务关闭时释放自身租约
```

连续三次续租失败后 Worker ID 标记无效，发号请求必须失败。Redis 的持久化、超时和网络隔离会直接影响可用性。

## 业务号段发号

```text
启动时每 60 秒加载有效 bizTag
→ 首次申请时原子推进数据库 max_id
→ 当前 Segment 内存自增
→ 使用超过 10% 后异步预取下一 Segment
→ 当前段耗尽后切换双缓冲
→ 根据 15/30 分钟消耗速度调整步长
```

数据库记录、`biz_tag` 唯一约束和更新 SQL 是跨节点不重复的核心；只测试内存算法不足以证明分布式安全。

## 国际化错误消息同步

```text
管理端保存或删除 ERROR_CODE 消息
→ MySQL 写入
→ EventPublisherHub 向 g2rain-syncer 发布 ERROR_MSG 创建/更新/删除事件
→ 消费方刷新本地错误消息缓存
```

数据库事务不能覆盖 Redis Stream 事件系统。当前没有 Outbox 或可见的失败补偿测试，详见[架构差异](deviations.md)。
