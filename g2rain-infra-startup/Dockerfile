FROM eclipse-temurin:25-jre-alpine
# 构建参数
ARG JAR_FILE
ARG BUILD_VERSION
# 环境变量
ENV APP_VERSION=${BUILD_VERSION}
# 复制应用
COPY ${JAR_FILE} app.jar
# 暴露端口
EXPOSE 8080
# 启动命令
ENTRYPOINT ["java", "-jar", "/app.jar"]
