package com.metallurgy.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PurchaseReportRow {
    private Long materialId;
    private String materialCode;
    private String materialName;
    private BigDecimal quantity;
    private String unit;
}
