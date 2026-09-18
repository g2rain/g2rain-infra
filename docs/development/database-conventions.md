# 数据库与数据模型

初始化脚本 `scripts/g2rain-infra.sql` 当前包含五张表：

- `dictionary_usage`
- `dictionary_item`
- `locale_setting`
- `i18n_message`
- `g2rain_raindrop`

这些是平台公共数据，当前没有 `organ_id`，不得在文档中虚构租户隔离。若未来引入租户级字典或文案，必须新增明确的租户模型、索引和迁移方案。

`dictionary_usage` 与 `dictionary_item` 使用逻辑删除但缺少业务唯一键。需要删除后释放业务键时，使用 MySQL 8.0.13+ 的 `IF(delete_flag = 0, 0, NULL)` 函数唯一索引；永久占位时唯一键不得包含 `delete_flag`。

`locale_setting.code` 和 `g2rain_raindrop.biz_tag` 当前是物理删除表上的永久唯一键。号段表的 `max_id` 更新必须保持单条原子 SQL 和版本条件，不能改为应用层读改写。

初始化脚本与生产增量迁移分离；变更需说明历史数据、锁表影响、执行顺序、验证和回滚。
