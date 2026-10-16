package com.metallurgy.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;

@TableName("material")
public class Material {
    @TableId private Long id;
    private String materialCode;
    private String materialName;
    private String category;
    private String unit;
    private Integer status;
    private BigDecimal mainIndexRequired;
    private BigDecimal sMax;
    private BigDecimal pMax;
    private String granularity;
    private BigDecimal unitPrice;
    private BigDecimal safetyStock;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaterialCode() { return materialCode; }
    public void setMaterialCode(String materialCode) { this.materialCode = materialCode; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public BigDecimal getMainIndexRequired() { return mainIndexRequired; }
    public void setMainIndexRequired(BigDecimal mainIndexRequired) { this.mainIndexRequired = mainIndexRequired; }
    public BigDecimal getSMax() { return sMax; }
    public void setSMax(BigDecimal sMax) { this.sMax = sMax; }
    public BigDecimal getPMax() { return pMax; }
    public void setPMax(BigDecimal pMax) { this.pMax = pMax; }
    public String getGranularity() { return granularity; }
    public void setGranularity(String granularity) { this.granularity = granularity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getSafetyStock() { return safetyStock; }
    public void setSafetyStock(BigDecimal safetyStock) { this.safetyStock = safetyStock; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
