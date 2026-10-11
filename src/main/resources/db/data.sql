USE metallurgy_procurement;
SET NAMES utf8mb4;
INSERT IGNORE INTO sys_user (username,password,real_name,role) VALUES
('admin','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','系统管理员','ADMIN'),
('caigou','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','采购员','PROCUREMENT'),
('zhijian','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldWy','质检员','INSPECTOR'),
('cangku','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldWy','库管员','WAREHOUSE');
INSERT IGNORE INTO material (material_code,material_name,category,main_index_required,s_max,p_max,granularity,unit_price,safety_stock) VALUES
('MAT-IRON-01','澳洲铁矿粉','IRON_ORE',62,0.050,0.100,'粒度≤10mm',850,1000),('MAT-COKE-01','一级焦炭','COKE',85,0.700,0.100,'25-80mm',2200,500),('MAT-FLUX-01','石灰石','FLUX',52,0.050,0.050,'10-40mm',180,300),('MAT-FLUX-02','白云石','FLUX',30,0.050,0.050,'10-40mm',240,300),('MAT-SCRAP-01','重型废钢','SCRAP',90,0.500,0.050,'≥6mm',2600,400),('MAT-FE-01','硅铁','FERROALLOY',75,0.030,0.050,'10-50mm',7200,100),('MAT-REF-01','高铝砖','REFRACTORY',80,0.100,0.050,'标准砖',12,1000),('MAT-IRON-02','球团矿','IRON_ORE',63,0.050,0.100,'8-16mm',930,800);
INSERT IGNORE INTO supplier (supplier_code,supplier_name,contact_person,phone,main_materials,status) VALUES ('SUP-001','力拓矿业贸易公司','张经理','13800000001','澳洲铁矿粉,球团矿',1),('SUP-002','山西焦化集团','李经理','13800000002','一级焦炭',1),('SUP-003','本地石灰石矿','王经理','13800000003','石灰石,白云石',1),('SUP-004','废钢回收公司','赵经理','13800000004','重型废钢',1);
INSERT IGNORE INTO inventory (material_id,quantity) VALUES (1,800),(2,600),(3,500),(4,100),(5,300),(6,80),(7,1200),(8,700);
INSERT IGNORE INTO purchase_plan (plan_code,material_id,quantity,expect_date,apply_dept,reason,applicant,apply_time,status,approver,approve_time,approve_remark) VALUES ('PP20261008001',1,500,'2026-10-20','炼铁一车间','补充高炉铁矿粉库存','李工',NOW(),'APPROVED','系统管理员',NOW(),'同意采购');
INSERT IGNORE INTO purchase_order (order_code,plan_id,supplier_id,material_id,quantity,unit_price,total_amount,order_date,expect_arrival_date,status) VALUES ('PO20261008001',1,1,1,500,850,425000,'2026-10-08','2026-10-20','CREATED');
