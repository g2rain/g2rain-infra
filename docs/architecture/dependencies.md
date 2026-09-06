# 模块与依赖边界

中央基线要求并且当前 POM 实现的方向是：

```text
g2rain-infra-startup → g2rain-infra-biz → g2rain-infra-api
```

- API 只依赖通用模型以及 Spring Web/Validation 契约，不引用 Biz 或 Startup。
- Biz 实现 API，依赖 MyBatis、MySQL、Redis、Aegis、Cache Sync 和 Stream Redis，不依赖 Startup。
- Startup 依赖 Biz，负责运行时组装，不向下层输出领域实现。
- Controller → Service → DAO；Converter、DAO 和 API 不反向依赖 Controller。
- App 写请求经 Gateway 到 Biz Controller；其他服务复用 API 查询和发号契约。

新增模块、依赖、同步写契约或反向引用时，必须更新 `docs/project.yaml`、本页和[架构差异](deviations.md)。
