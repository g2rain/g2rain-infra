#!/usr/bin/env bash
set -e

# 切到脚本所在目录（确保在 g2rain-infra 根目录执行）
cd "$(dirname "$0")"

APP_IMAGE="g2rain/g2rain-infra"

# 第一个参数可选：指定 tag；不传时默认 latest
TAG="${1:-latest}"

echo "Building Docker image: ${APP_IMAGE}:${TAG}"

# 先在根目录编译并安装整个项目（包括所有子模块）
mvn -DskipTests=true clean install

# 然后进入 startup 子模块，执行 Jib 构建
cd g2rain-infra-startup

mvn -DskipTests=true \
  compile jib:dockerBuild \
  -Djib.to.image=${APP_IMAGE}:${TAG}
