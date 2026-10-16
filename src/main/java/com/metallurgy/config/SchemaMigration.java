package com.metallurgy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Applies small, idempotent compatibility migrations for databases created by older schema.sql files. */
@Component
public class SchemaMigration implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(SchemaMigration.class);
    private final JdbcTemplate jdbcTemplate;

    public SchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.columns "
                            + "WHERE table_schema = DATABASE() AND table_name = 'material' AND column_name = 'status'",
                    Integer.class);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE material ADD COLUMN status TINYINT NOT NULL DEFAULT 1 AFTER unit");
                log.info("已补齐 material.status 数据库字段");
            }
            seedDemoPurchaseOrders();
        } catch (DataAccessException ex) {
            log.error("数据库兼容迁移失败: exception={}, rootCause={}", ex.getClass().getSimpleName(),
                    rootCause(ex).getClass().getSimpleName());
        }
    }

    private void seedDemoPurchaseOrders() {
        jdbcTemplate.execute("INSERT IGNORE INTO purchase_order "
                + "(order_code,plan_id,supplier_id,material_id,quantity,unit_price,total_amount,order_date,expect_arrival_date,status) "
                + "SELECT seed.order_code,NULL,s.id,m.id,seed.quantity,m.unit_price,"
                + "ROUND(seed.quantity * m.unit_price,2),seed.order_date,"
                + "DATE_ADD(seed.order_date, INTERVAL 10 DAY),'CREATED' "
                + "FROM ("
                + "SELECT 'PO20260215001' order_code,'MAT-COKE-01' material_code,'SUP-002' supplier_code,320 quantity,'2026-02-15' order_date UNION ALL "
                + "SELECT 'PO20260310001','MAT-FLUX-01','SUP-003',1200,'2026-03-10' UNION ALL "
                + "SELECT 'PO20260408001','MAT-FLUX-02','SUP-003',750,'2026-04-08' UNION ALL "
                + "SELECT 'PO20260517001','MAT-SCRAP-01','SUP-004',280,'2026-05-17' UNION ALL "
                + "SELECT 'PO20260612001','MAT-FE-01','SUP-005',35,'2026-06-12' UNION ALL "
                + "SELECT 'PO20260721001','MAT-REF-01','SUP-006',24,'2026-07-21' UNION ALL "
                + "SELECT 'PO20260816001','MAT-IRON-02','SUP-001',420,'2026-08-16'"
                + ") seed JOIN material m ON m.material_code=seed.material_code "
                + "JOIN supplier s ON s.supplier_code=seed.supplier_code");
        log.info("已检查演示采购订单数据");
    }

    private Throwable rootCause(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) root = root.getCause();
        return root;
    }
}
