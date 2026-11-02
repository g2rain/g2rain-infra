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

脚本先执行 `mvn clean install`（包含当前 Maven 测试阶段），再通过 Jib 构建 `ghcr.io/g2rain/g2rain-infra:<tag>`。本地模式调用 `jib:dockerBuild`，需要 Docker daemon 且不推送；`./build.sh <tag> push` 调用 `jib:build`，使用已由 `docker login` 保存的凭据直接推送，不需要 Docker daemon。两种模式使用同一 Jib 配置、Java 25 JRE、启动类与端口 `8080`。完整 Maven 构建不等同于运行时依赖、容器启动或部署验证。

## 容器镜像发布（GHCR）

镜像地址为 `ghcr.io/g2rain/g2rain-infra:<tag>`。`.github/workflows/image.yml` 只会在推送精确格式的稳定 tag（如 `v1.0.0`）时触发，普通分支推送不会触发；工作流还会验证 tag 提交位于 `main` 历史，并检查同提交存在非空的 `docs/releases/v<版本>.md`。随后以 Temurin JDK 25 运行 `mvn clean install`，再借助 Jib 推送版本标签与 `sha-<完整提交 SHA>`。不会创建 GitHub Release、部署服务或更新 `latest`。

认证使用 Actions 提供的 `GITHUB_TOKEN`，仅授予 `contents: read` 和 `packages: write`，不需要 PAT Secret。首次成功推送后，在 **Packages → g2rain-infra → Package settings → Change visibility → Public** 设为 Public，后续版本沿用该可见性；若组织策略禁止修改，请联系组织管理员。若推送返回 `denied`，即使 Docker 登录、构建或部分 layer 上传成功，也应在包的 **Actions access** 授予 `g2rain/g2rain-infra` 写入权限，并核对组织 Packages 策略。

发布顺序为：将发布改动和同名说明按 `feature/* → develop → main` 合入、在 main 提交上验证、创建并推送 `v1.0.0`、确认摘要中的两个标签成功，再按环境部署指定版本。仅在包已公开后，部署主机才能匿名执行 `docker pull ghcr.io/g2rain/g2rain-infra:1.0.0`；私有包要求部署凭据有 `read:packages`。

## 发布检查

- MySQL 结构已按增量迁移升级，`g2rain_raindrop` 至少包含所需 bizTag。
- Redis 可用、环境隔离且持久化策略满足 Worker 租约要求。
- Nacos namespace、group、服务名和凭据正确。
- `g2rain-syncer` 通道与消费方兼容。
- 敏感配置由部署环境注入。
- 健康检查、发号、错误消息事件和回滚流程已验证。
