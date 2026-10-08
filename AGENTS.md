# AGENTS.md

## 项目概览

本仓库是“冶金企业原材料采购管理系统”的 Spring Boot 后端，当前处于阶段 1：完成工程骨架、数据库脚本、通用响应/异常处理和登录接口占位；采购、检验、仓储等业务模块将按阶段逐步补齐。

技术基线：JDK 8+、Spring Boot 2.7.18、Spring Web、Bean Validation、MyBatis-Plus 3.5.5、MySQL 8.0、Lombok、JJWT 0.11.5。项目不使用 Redis、消息队列、微服务、Docker 或 Spring Cloud。

## 目录与职责

- 项目是 Spring Boot 2.7.18 单模块后端，使用 JDK 8+、Maven、MySQL 8.0、MyBatis-Plus 3.5.5。当前为阶段 1，登录接口仅占位。

## 结构

- `pom.xml`：Maven 依赖与 Spring Boot 插件，无 Maven Wrapper。
- `src/main/java/com/metallurgy/MetallurgyApplication.java`：启动入口。
- `auth/`：认证接口（`/api/auth/login` 当前不校验用户或签发 JWT）。
- `common/`：统一响应、业务异常、全局异常处理。
- `config/`：CORS 和 MyBatis-Plus 分页配置。
- `controller/`、`dto/vo/`、`entity/`、`mapper/`、`service/`：后续业务扩展位置；跨表事务放在 service。
- `src/main/resources/application.yml`：端口、MySQL 数据源、MyBatis-Plus、日志。
- `src/main/resources/db/schema.sql`、`data.sql`：建库建表及演示数据，先 schema 后 data。

核心表：`sys_user`、`material`、`supplier`、`purchase_plan`、`purchase_order`、`arrival_inspect`、`inbound`、`inventory`、`outbound`。

## 开发与验证

先在 `application.yml` 修改数据库配置；`password: change-me` 只是示例，禁止提交真实凭据。启动：`mvn spring-boot:run`，默认 `http://localhost:8080`。测试：`mvn test`；打包：`mvn package`。当前无测试源码，新增行为应在 `src/test/java` 补测试。

所有 API 使用 `/api` 前缀并返回 `ApiResponse` 的 `{code, msg, data}` 结构；业务错误抛出 `BusinessException`。请求使用 Bean Validation，分页参数统一 `pageNum`、`pageSize`。数据库下划线字段依赖 MyBatis-Plus 转驼峰，改表时同步更新 SQL 与实体/Mapper。

## 提交前检查

- 保持小而可审查的改动，检查 `git diff`，避免无关重构。
- 行为变化补测试并运行 `mvn test`。
- 数据库改动在干净实例按 `schema.sql` 后 `data.sql` 验证。
- 不提交编译产物、IDE 私有配置、密码、Token 或私钥。
