# 冶金企业原材料采购管理系统后端

本项目是“冶金企业原材料采购管理系统”的 Spring Boot 后端，面向钢铁等冶金企业的原材料采购、到货检验和库存管理场景。后端统一提供 REST API，并负责业务校验、数据库访问、异常处理以及后续的角色权限控制。

当前代码处于阶段 1：已完成可扩展的后端工程骨架、数据库脚本和通用配置；登录及采购、检验、仓储等完整业务接口将在后续阶段逐步实现。

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
- `/api/auth/login` 登录接口形状占位，真实用户校验和 JWT 将在阶段 2 实现。
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

先在 `application.yml` 中修改本机 MySQL 用户名和密码，然后执行：

```sql
SOURCE src/main/resources/db/schema.sql;
SOURCE src/main/resources/db/data.sql;
```

### 2. 启动后端

在 `backend` 目录执行：

```bash
mvn spring-boot:run
```

默认服务地址为 `http://localhost:8080`。

## 后续开发计划

1. 接入 BCrypt 用户查询、JWT 生成和拦截器。
2. 实现物料档案、供应商管理 CRUD。
3. 实现采购计划审批和计划转订单。
4. 实现订单到货、检验自动判定、合格入库和库存联动。
5. 实现领用出库、安全库存预警。
6. 提供仪表盘和 ECharts 报表聚合接口。
7. 完成端到端业务闭环联调。

## 当前限制

- 项目不包含 Maven Wrapper，需要本机安装 Maven。
- `application.yml` 中的数据库密码是示例配置，运行前必须替换为本机配置。
- 阶段 1 的登录接口仅保留接口形状，尚未执行真实账号校验，也不会签发可用 JWT。
- controller、service、entity 和 mapper 业务实现会在后续阶段补充。
