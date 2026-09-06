# CRUD 代码生成

根 POM 配置 `g2rain-crafter`，入口为：

```bash
mvn g2rain-crafter:bootstrap
```

生成前：

1. 在独立分支提交或暂存现有修改。
2. 将 `codegen.properties` 的 `database.tables` 收敛到本次真实存在的表。
3. 通过私有配置提供数据库凭据，并保持 `tables.overwrite=false`。
4. 当前 `route_definition` 与 `i18n_message_usage` 没有对应源码/初始化表，不得直接生成。

生成后逐文件检查 Git Diff，并补充数据库唯一约束、领域校验、事务、授权、事件一致性和测试。生成代码不能替代领域设计。
