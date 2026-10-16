# 冶金企业原材料采购管理系统后端

本项目是“冶金企业原材料采购管理系统”的 Spring Boot 后端，面向钢铁等冶金企业的原材料采购、到货检验和库存管理场景。后端统一提供 REST API，并负责业务校验、数据库访问、异常处理以及后续的角色权限控制。

当前已实现账号注册、用户名/邮箱登录、JWT 会话查询，以及物料、供应商和采购报表接口。采购计划、检验、仓储等业务接口仍按阶段逐步补齐。

## 技术栈

- JDK 8+
- Spring Boot 2.7.18
- Spring Web、Bean Validation
- MyBatis-Plus 3.5.5
- MySQL 8.0
- Lombok
- Spring Security Crypto（用于 BCrypt 密码处理）
- JJWT 0.11.5（为后续 JWT 鉴权准备）

项目不引入 Redis、消息队列、微服务、Docker 或 Spring Cloud，保持课程作业所需的简单部署方式。

## 当前已实现

- Spring Boot 应用启动类 `MetallurgyApplication`。
- 统一响应结构：`{ code, msg, data }`。
- `BusinessException` 和全局异常处理器。
- `/api/**` 跨域配置，允许本地前端开发服务器访问。
- MyBatis-Plus MySQL 分页插件和下划线转驼峰配置。
- `/api/auth/register`、`/api/auth/login`、`/api/auth/me`、`/api/auth/logout` 认证接口；密码使用 BCrypt 存储，登录签发 JWT。
- 物料与供应商管理接口，以及按年度聚合的采购报表接口。
- 9 张核心业务表的建表脚本和初始化演示数据。

## 目录结构

```text
backend/
├─ pom.xml
└─ src/main/
   ├─ java/com/metallurgy/
   │  ├─ MetallurgyApplication.java  # 应用入口
   │  ├─ auth/                       # 登录、JWT、角色鉴权
   │  ├─ common/                     # 统一响应、业务异常、全局异常
   │  ├─ config/                     # Web、CORS、MyBatis-Plus 配置
   │  ├─ controller/                 # REST 控制器（后续补充）
   │  ├─ dto/vo/                     # 请求和响应对象（后续补充）
   │  ├─ entity/                     # 数据库实体（后续补充）
   │  ├─ mapper/                     # MyBatis-Plus Mapper（后续补充）
   │  └─ service/                    # 业务服务和事务（后续补充）
   └─ resources/
      ├─ application.yml             # 服务和数据库配置
      └─ db/
         ├─ schema.sql                # 建库建表脚本
         └─ data.sql                  # 初始化和演示数据
```

## 数据库

执行 `src/main/resources/db/schema.sql` 后，会创建 `metallurgy_procurement` 数据库及以下 9 张表：

`sys_user`、`material`、`supplier`、`purchase_plan`、`purchase_order`、`arrival_inspect`、`inbound`、`inventory`、`outbound`。

`data.sql` 提供初始化用户、8 种物料、4 家供应商、库存数据，以及一条已审批计划和 CREATED 状态订单，方便后续演示采购业务闭环。脚本使用业务 ID 关联，未设置物理外键。

## 接口约定

- 所有接口使用 `/api` 前缀。
- 成功响应格式为：

  ```json
  {
    "code": 200,
    "msg": "success",
    "data": {}
  }
  ```

- 业务异常由全局异常处理器统一转换为中文错误响应。
- 后续分页接口统一采用 `pageNum`、`pageSize` 和业务关键词参数。

计划提供的核心接口包括登录、物料和供应商 CRUD、采购计划审批、采购订单、到货检验、入库、出库、库存预警、仪表盘和统计报表。

## 本地运行

### 1. 准备数据库

启动本机 MySQL 8.0，并使用有权创建数据库/表的账号执行脚本。先在系统终端进入仓库 `backend` 目录，再从该目录启动 MySQL 命令行客户端（例如 `mysql -u root -p`），然后在 MySQL 提示符中依次执行：

```sql
SOURCE src/main/resources/db/schema.sql;
SOURCE src/main/resources/db/data.sql;
```

`schema.sql` 会创建 `metallurgy_procurement` 数据库及所需表，并包含 `sys_user.email` 等兼容迁移；`data.sql` 写入演示账号和业务数据。已有数据库也应先执行 `schema.sql`，确保旧表具备当前认证代码需要的列。

若数据库已经初始化，只需补充其他物料的当年采购演示数据，可在 MySQL 提示符中单独执行 `SOURCE src/main/resources/db/demo_purchase_orders.sql;`。该脚本只写入两家缺失供应商和 7 条采购订单，订单编码固定并使用 `INSERT IGNORE`，可重复执行。

后端默认连接 `localhost:3306/metallurgy_procurement`，默认用户名为 `root`。密码不会写在仓库中，必须通过运行进程的 `DB_PASSWORD` 环境变量提供；也可用 `DB_URL`、`DB_USERNAME` 覆盖默认地址和用户名。启动进程必须实际继承这些变量：在 IDE 中运行时，将它们配置到 Spring Boot Run Configuration 的 Environment variables；在 PowerShell 中可在同一窗口设置后启动：

```powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/metallurgy_procurement?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai'
$env:DB_USERNAME = 'root'
$env:DB_PASSWORD = '<本机 MySQL 密码>'
mvn spring-boot:run
```

请把 `<本机 MySQL 密码>` 替换为本机值，不要提交到代码、README 或版本控制。若账号无密码，按本机 MySQL 安全策略设置空值；生产环境应使用专用、最小权限账号。

### 2. 启动后端

先在 `backend` 目录启动后端：

```bash
mvn spring-boot:run
```

默认服务地址为 `http://localhost:8080`。再在另一个终端进入同级 `frontend` 目录运行 `npm run dev`；Vite 会把 `/api` 请求代理到 `http://localhost:8080`。如果 8080 已被其他进程占用，先停止旧后端实例，或通过 `SERVER_PORT` 为后端指定其他端口并同步调整 `frontend/vite.config.js` 代理地址。

注册/登录时报“数据库暂不可用”时，依次确认：MySQL 服务正在运行且 3306 可连接；`DB_URL` 指向正确数据库；`DB_USERNAME`/`DB_PASSWORD` 对应本机账号；已按顺序执行 `schema.sql`、`data.sql`；从 IDE 启动时环境变量已配置在该 IDE 的运行配置中。

## 后续开发计划

1. 实现采购计划审批和计划转订单。
2. 实现订单到货、检验自动判定、合格入库和库存联动。
3. 实现领用出库、安全库存预警。
4. 补齐仪表盘和其他统计报表聚合接口。
5. 完成端到端业务闭环联调。

## 当前限制

- 项目不包含 Maven Wrapper，需要本机安装 Maven。
- 数据库连接密码必须由运行环境提供，仓库不包含本机或生产凭据。
- 注册和登录依赖已初始化的 MySQL 表结构；唯一键是防止并发重复注册的最终保障。
- 采购计划、检验和仓储等完整业务接口仍在后续阶段补充。
