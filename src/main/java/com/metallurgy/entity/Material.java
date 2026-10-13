package com.metallurgy.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
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
}
