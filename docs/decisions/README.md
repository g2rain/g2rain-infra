# 架构决策记录

本目录记录只影响 `g2rain-infra` 的长期架构决策。组织级规则由中央 g2rain ADR 与 `java-domain-service` Profile 管理。

新增 ADR 建议使用 `NNNN-title.md`，包含状态、背景、决策、影响、替代方案、迁移与回滚。以下变化通常需要 ADR：

- 更换 Snowflake Worker 租约协议或 ID 位布局。
- 更换号段算法、数据库原子更新或一致性策略。
- 引入 Outbox 等数据库—事件一致性机制。
- 把公共元数据改为租户级数据。
- 新增同步写契约或调整模块边界。
