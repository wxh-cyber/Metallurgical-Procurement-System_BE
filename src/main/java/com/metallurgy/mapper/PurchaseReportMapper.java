package com.metallurgy.mapper;

import com.metallurgy.dto.PurchaseReportRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface PurchaseReportMapper {
    @Select("SELECT po.material_id AS material_id, m.material_code, m.material_name, SUM(po.quantity) AS quantity, m.unit " +
            "FROM purchase_order po JOIN material m ON m.id = po.material_id " +
            "WHERE po.order_date IS NOT NULL AND YEAR(po.order_date) = #{year} " +
            "GROUP BY po.material_id, m.material_code, m.material_name, m.unit ORDER BY quantity DESC")
    List<PurchaseReportRow> byYear(@Param("year") Integer year);
}
