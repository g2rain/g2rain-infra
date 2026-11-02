# 发布记录

- [v1.0.0：Infra 首个稳定版与 GHCR 镜像发布准备](v1.0.0.md)

源代码按 `feature/* → develop → main` 的 Pull Request 流程集成。准备发布时，先将 `develop` 通过 Pull Request 合入 `main`；在已验证的 main 提交上创建 `v<major>.<minor>.<patch>` 标签。稳定版 tag 推送会触发 GHCR 镜像构建与发布；不会自动创建 GitHub Release 或部署。
