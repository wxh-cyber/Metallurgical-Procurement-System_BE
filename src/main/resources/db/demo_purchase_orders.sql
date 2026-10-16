USE metallurgy_procurement;
SET NAMES utf8mb4;

-- 可重复执行的演示采购数据脚本。前提：已执行 schema.sql 和基础 data.sql。
INSERT IGNORE INTO supplier (supplier_code,supplier_name,contact_person,phone,main_materials,status) VALUES
  ('SUP-005','硅铁合金供应商',NULL,NULL,'硅铁',1),
  ('SUP-006','耐火材料供应商',NULL,NULL,'高铝砖',1);

INSERT IGNORE INTO purchase_order
  (order_code,plan_id,supplier_id,material_id,quantity,unit_price,total_amount,order_date,expect_arrival_date,status)
SELECT seed.order_code,NULL,s.id,m.id,seed.quantity,m.unit_price,
       ROUND(seed.quantity * m.unit_price,2),seed.order_date,
       DATE_ADD(seed.order_date, INTERVAL 10 DAY),'CREATED'
FROM (
  SELECT 'PO20260215001' AS order_code,'MAT-COKE-01' AS material_code,'SUP-002' AS supplier_code,320 AS quantity,'2026-02-15' AS order_date UNION ALL
  SELECT 'PO20260310001','MAT-FLUX-01','SUP-003',1200,'2026-03-10' UNION ALL
  SELECT 'PO20260408001','MAT-FLUX-02','SUP-003',750,'2026-04-08' UNION ALL
  SELECT 'PO20260517001','MAT-SCRAP-01','SUP-004',280,'2026-05-17' UNION ALL
  SELECT 'PO20260612001','MAT-FE-01','SUP-005',35,'2026-06-12' UNION ALL
  SELECT 'PO20260721001','MAT-REF-01','SUP-006',24,'2026-07-21' UNION ALL
  SELECT 'PO20260816001','MAT-IRON-02','SUP-001',420,'2026-08-16'
) AS seed
JOIN material m ON m.material_code = seed.material_code
JOIN supplier s ON s.supplier_code = seed.supplier_code;
