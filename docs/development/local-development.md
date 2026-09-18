# 本地开发

## 环境

- JDK 25
- Maven 3.9+
- MySQL 8.0.13+
- Redis
- Nacos

执行 `scripts/g2rain-infra.sql` 初始化数据库。使用独立开发库和 namespace，不要复用生产凭据。

## 构建与启动

```bash
mvn clean verify
mvn -pl g2rain-infra-startup -am spring-boot:run
```

默认端口 `8080`、profile `dev`。Snowflake Bean 启动时必须从 Redis 取得 Worker ID；Redis 不可用会使应用启动失败。

## 调试入口

- 健康检查：`GET /actuator/health`
- MyBatis Mapper：`g2rain-infra-biz/src/main/resources/mybatis/mapper`
- 初始化 SQL：`scripts/g2rain-infra.sql`
- OpenAPI：由 `g2rain-starter-spring-doc` 提供，实际地址受平台配置影响。
