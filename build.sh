#!/usr/bin/env bash
set -euo pipefail

# 切到脚本所在目录（确保在 g2rain-infra 根目录执行）
cd "$(dirname "$0")"

APP_IMAGE="${APP_IMAGE:-ghcr.io/g2rain/g2rain-infra}"

# 第一个参数可选：指定 tag；不传时默认 latest
TAG="${1:-latest}"
MODE="${2:-local}"

if [[ "$MODE" != "local" && "$MODE" != "push" ]]; then
  echo "Usage: $0 [tag] [local|push]" >&2
  exit 2
fi
if [[ ! "$TAG" =~ ^[a-zA-Z0-9_][a-zA-Z0-9_.-]{0,127}$ ]]; then
  echo "Invalid image tag: $TAG" >&2
  exit 2
fi

echo "Building Docker image: ${APP_IMAGE}:${TAG}"

# 先在根目录编译并安装整个项目（包括所有子模块）
mvn --batch-mode --no-transfer-progress clean install

# 然后进入 startup 子模块，执行 Jib 构建
cd g2rain-infra-startup

args=("-Djib.to.image=${APP_IMAGE}:${TAG}")
if [[ -n "${EXTRA_TAGS:-}" ]]; then
  args+=("-Djib.to.tags=${EXTRA_TAGS}")
fi

if [[ "$MODE" == "push" ]]; then
  # Jib reuses credentials written by `docker login` and does not need a Docker daemon.
  mvn --batch-mode --no-transfer-progress -DskipTests=true compile jib:build "${args[@]}"
else
  # jib:dockerBuild needs a local Docker daemon and does not push to a registry.
  mvn --batch-mode --no-transfer-progress -DskipTests=true compile jib:dockerBuild "${args[@]}"
fi
