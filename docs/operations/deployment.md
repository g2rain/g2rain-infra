# 构建与部署

## 验证与产物

```bash
mvn clean verify
mvn clean package
```

可执行 Jar：`g2rain-infra-startup/target/g2rain-infra-startup-1.0.0.jar`。

## 镜像

```bash
./build.sh 1.0.0
```

脚本先以 `-DskipTests=true` 安装模块，再通过 Jib 构建 `g2rain/g2rain-infra:<tag>`；因此发布前必须单独运行完整验证。Jib 与 Dockerfile 均声明 Java 25 和容器端口 `8080`。

## 发布检查

- MySQL 结构已按增量迁移升级，`g2rain_raindrop` 至少包含所需 bizTag。
- Redis 可用、环境隔离且持久化策略满足 Worker 租约要求。
- Nacos namespace、group、服务名和凭据正确。
- `g2rain-syncer` 通道与消费方兼容。
- 敏感配置由部署环境注入。
- 健康检查、发号、错误消息事件和回滚流程已验证。
