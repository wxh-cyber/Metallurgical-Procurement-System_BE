# 冶金企业原材料采购管理系统后端接口文档

> 版本：阶段 1 接口契约草案｜更新：2026-10-08

本文档以 Spring Boot 后端、前端页面规划和 `src/main/resources/db/schema.sql` 为依据。接口状态分为：**已实现**（当前代码可调用）和**规划中**（用于前后端联调的目标契约，当前尚未实现）。

## 1. 基础约定

- 基础路径：`/api`；请求和响应均使用 `application/json`。
- 当前服务端口：`8080`，开发环境由前端 Vite 将 `/api` 代理到后端。
- 认证：规划接口使用 `Authorization: Bearer <token>`；当前阶段登录返回占位消息，不签发 JWT。
- 成功响应：`{ "code": 200, "msg": "success", "data": ... }`。
- 失败响应：`{ "code": <业务码>, "msg": "错误描述", "data": null }`。
- 推荐错误码：`400` 参数校验失败，`401` 未认证/令牌失效，`403` 无权限，`404` 资源不存在，`409` 状态或唯一键冲突，`500` 系统异常。
- 分页请求统一使用 `pageNum`（从 1 开始）和 `pageSize`（建议 1～100）；规划分页响应为 `{ records, total, pageNum, pageSize, pages }`。

## 2. 认证

### `POST /api/auth/login`（已实现：占位）

请求：

```json
{
  "username": "admin",
  "password": "******"
}
```

`username`、`password` 必填且不可为空。当前实现仅校验 Bean Validation，并返回：

```json
{
  "code": 200,
  "msg": "success",
  "data": { "message": "登录接口将在阶段 2 实现" }
}
```

阶段 2 目标响应：`data.token`、`data.expiresIn`、`data.user`（`id`、`username`、`realName`、`role`）。前端当前登录页字段名为 `account`，接入时应映射为 `username`。

### `POST /api/auth/register`（规划中）

请求字段：`username`、`email`、`password`；用户名和邮箱唯一，密码至少 6 位。成功返回创建后的用户概要，不返回密码。

## 3. 业务接口总览

除认证外，以下接口均为规划中接口；列表接口支持 `pageNum`、`pageSize` 及模块筛选字段，单条接口使用路径参数 `id`。角色：`ADMIN` 管理员、`PROCUREMENT` 采购员、`INSPECTOR` 质检员、`WAREHOUSE` 库管员。

| 模块 | 方法与路径 | 允许角色 | 主要用途 |
|---|---|---|---|
| 仪表盘 | `GET /dashboard/summary` | 全部 | 待办、库存预警、订单和金额概览 |
| 物料 | `GET /materials`、`GET /materials/{id}` | 全部 | 分页查询、详情 |
| 物料 | `POST /materials`、`PUT /materials/{id}`、`DELETE /materials/{id}` | ADMIN | 维护物料档案 |
| 供应商 | `GET /suppliers`、`GET /suppliers/{id}` | 全部 | 查询供应商 |
| 供应商 | `POST /suppliers`、`PUT /suppliers/{id}`、`PATCH /suppliers/{id}/status` | ADMIN、PROCUREMENT | 维护与启停供应商 |
| 采购计划 | `GET /purchase-plans`、`POST /purchase-plans` | ADMIN、PROCUREMENT | 查询和创建计划 |
| 采购计划 | `POST /purchase-plans/{id}/submit`、`POST /purchase-plans/{id}/approve` | PROCUREMENT、ADMIN | 提交与审批 |
| 采购订单 | `GET /purchase-orders`、`GET /purchase-orders/{id}`、`POST /purchase-orders` | ADMIN、PROCUREMENT | 订单查询与创建 |
| 采购订单 | `POST /purchase-orders/{id}/status` | ADMIN、PROCUREMENT | 更新订单状态 |
| 到货检验 | `GET /arrival-inspects`、`POST /arrival-inspects` | INSPECTOR、ADMIN | 到货登记与检验 |
| 入库 | `GET /inbounds`、`POST /inbounds` | WAREHOUSE、ADMIN | 合格物料入库 |
| 出库 | `GET /outbounds`、`POST /outbounds` | WAREHOUSE、ADMIN | 领用出库 |
| 库存 | `GET /inventories`、`GET /inventories/warnings` | 全部、WAREHOUSE | 台账与安全库存预警 |
| 报表 | `GET /reports/purchase`、`GET /reports/suppliers`、`GET /reports/monthly`、`GET /reports/inspection` | ADMIN、PROCUREMENT、INSPECTOR | 统计图表数据 |

## 4. 请求字段与状态

### 4.1 物料 `material`

创建/更新字段：`materialCode`、`materialName`、`category`、`unit`、`mainIndexRequired`、`sMax`、`pMax`、`granularity`、`unitPrice`、`safetyStock`、`remark`。列表支持 `keyword`、`category`。

### 4.2 供应商 `supplier`

创建/更新字段：`supplierCode`、`supplierName`、`contactPerson`、`phone`、`address`、`mainMaterials`、`businessLicense`、`status`、`remark`。`status`：`1` 启用，`0` 停用。

### 4.3 采购计划 `purchase_plan`

创建字段：`materialId`、`quantity`、`expectDate`、`applyDept`、`reason`、`applicant`。状态：`PENDING` → `SUBMITTED` → `APPROVED` / `REJECTED`；审批字段：`approver`、`approveTime`、`approveRemark`。

### 4.4 采购订单 `purchase_order`

创建字段：`planId`、`supplierId`、`materialId`、`quantity`、`unitPrice`、`orderDate`、`expectArrivalDate`；服务端计算 `totalAmount = quantity × unitPrice`，禁止由客户端直接覆盖。状态建议：`CREATED` → `SENT` → `PARTIAL_RECEIVED` → `RECEIVED` / `CANCELLED`。

### 4.5 到货检验 `arrival_inspect`

字段：`orderId`、`materialId`、`arrivalDate`、`arrivalQuantity`、`mainIndexActual`、`sActual`、`pActual`、`granularityOk`、`result`、`inspector`、`inspectTime`、`remark`。`result`：`PASS`、`FAIL`、`PENDING`。只有 `PASS` 才能创建入库单。

### 4.6 入库、出库与库存

- 入库 `POST /inbounds`：`inspectId`、`orderId`、`materialId`、`quantity`、`inTime`、`operator`、`remark`；必须关联合格检验，成功后增加 `inventory.quantity`。
- 出库 `POST /outbounds`：`materialId`、`quantity`、`dept`、`outTime`、`operator`、`remark`；库存不足返回 `409`，成功后扣减库存。
- 库存字段：`materialId`、`quantity`、`updateTime`；预警条件为 `quantity < material.safetyStock`。

## 5. 统计接口

- `GET /api/reports/purchase?startDate=&endDate=`：返回采购数量、采购金额及按物料聚合数据。
- `GET /api/reports/suppliers?startDate=&endDate=`：返回供应商订单数、数量和金额。
- `GET /api/reports/monthly?year=`：返回按月份的采购金额/数量序列。
- `GET /api/reports/inspection?startDate=&endDate=`：返回检验总数、合格数、不合格数和合格率。

图表接口返回纯数据，不返回格式化后的颜色或组件配置；日期参数使用 `yyyy-MM-dd`。

## 6. 数据与联调约束

- 业务表主键均为 `id`；外键关系：订单关联计划、供应商、物料，检验关联订单和物料，入库关联检验/订单/物料，库存和出库关联物料。
- 数据库下划线字段通过 MyBatis-Plus 自动映射为驼峰字段。
- 所有写操作应校验唯一编码（物料、供应商、计划、订单、检验、入库、出库）。
- 当前数据库脚本含演示用户、物料、供应商、库存、计划和订单数据；密码和数据仅用于本地演示，不得作为生产凭据。
