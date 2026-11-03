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

镜像地址为 `ghcr.io/g2rain/g2rain-infra:<tag>`。`.github/workflows/publish-ghcr.yml` 只会在推送精确格式的稳定 tag（如 `v1.0.0`）时触发，普通分支推送不会触发；工作流还会验证 tag 提交位于 `main` 历史、同提交存在非空的 `docs/releases/v<版本>.md`，且 tag 版本与 Maven 项目版本一致。随后以 Temurin JDK 25 在根目录运行 `mvn clean install`，再在 `g2rain-infra-startup` 内调用 Jib 推送版本标签与 `sha-<完整提交 SHA>`。不会创建 GitHub Release、部署服务或更新 `latest`。

认证使用 Actions 提供的 `GITHUB_TOKEN`，仅授予 `contents: read` 和 `packages: write`，不需要 PAT Secret。首次成功推送后，在 **Packages → g2rain-infra → Package settings → Change visibility → Public** 设为 Public，后续版本沿用该可见性；若组织策略禁止修改，请联系组织管理员。若推送返回 `denied`，即使 Docker 登录、构建或部分 layer 上传成功，也应在包的 **Actions access** 授予 `g2rain/g2rain-infra` 写入权限，并核对组织 Packages 策略。

发布顺序为：将发布改动和同名说明按 `feature/* → develop → main` 合入、在 main 提交上验证、创建并推送 `v1.0.0`、确认摘要中的两个标签成功，再按环境部署指定版本。仅在包已公开后，部署主机才能匿名执行 `docker pull ghcr.io/g2rain/g2rain-infra:1.0.0`；私有包要求部署凭据有 `read:packages`。

## API 发布到 GitHub Maven Packages

`.github/workflows/publish-maven.yml` 是独立可复用工作流，提供 **Run workflow** 手动入口并可由其他工作流调用。它只接受 `main` 历史中的提交，以 Temurin JDK 25 和 Maven 执行 `mvn --batch-mode --no-transfer-progress -pl g2rain-infra-api -am clean deploy -DaltDeploymentRepository=github::https://maven.pkg.github.com/g2rain/g2rain-infra`。Reactor 范围仅为根 `g2rain-infra` 父 POM 与 `g2rain-infra-api`：不会选择 Biz 或 Startup；API 模块会随既有 `maven-source-plugin` 发布 sources，根现有 Flatten 配置会在发布生命周期中解析 `${revision}`。

稳定 tag 的 GHCR 工作流会先完成镜像构建和两个镜像标签推送，再调用该 Maven 工作流；Maven 上传失败会使工作流失败，但不会撤回已成功发布的镜像，两种包发布不是事务性操作。手动发布与自动发布使用同一个 Maven 并发组串行化。GitHub Maven Packages 不像公开 GHCR 镜像可匿名拉取：消费者应在 Maven `settings.xml` 配置 `https://maven.pkg.github.com/g2rain/g2rain-infra` 及认证。跨仓库使用 `GITHUB_TOKEN` 需为消费仓库授予包访问权限；个人读取时使用有 `read:packages` 权限的 PAT classic，并通过受控的 settings 或环境变量注入，绝不提交 Token。

GitHub Maven Packages 通常拒绝重复的同版本稳定制品；不要删除或覆盖已发布版本，按仓库版本策略升级后再发布。本地构建、CI 构建、GHCR 推送、Maven 上传和消费者解析是独立验证项，只有各自实际完成后才能记录为通过。

## 发布检查

- MySQL 结构已按增量迁移升级，`g2rain_raindrop` 至少包含所需 bizTag。
- Redis 可用、环境隔离且持久化策略满足 Worker 租约要求。
- Nacos namespace、group、服务名和凭据正确。
- `g2rain-syncer` 通道与消费方兼容。
- 敏感配置由部署环境注入。
- 健康检查、发号、错误消息事件和回滚流程已验证。
