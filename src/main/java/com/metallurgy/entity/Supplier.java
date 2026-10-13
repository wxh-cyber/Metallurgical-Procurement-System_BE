package com.metallurgy.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("supplier")
public class Supplier {
    @TableId private Long id;
    private String supplierCode;
    private String supplierName;
    private String contactPerson;
    private String phone;
    private String address;
    private String mainMaterials;
    private String businessLicense;
    private Integer status;
    private String remark;
}
