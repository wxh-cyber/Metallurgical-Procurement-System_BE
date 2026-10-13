package com.metallurgy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.metallurgy.common.ApiResponse;
import com.metallurgy.entity.Supplier;
import com.metallurgy.mapper.SupplierMapper;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierMapper mapper;
    public SupplierController(SupplierMapper mapper) { this.mapper = mapper; }

    @GetMapping
    public ApiResponse<?> list(@RequestParam(defaultValue = "1") long pageNum, @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) String keyword) {
        QueryWrapper<Supplier> query = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) query.and(w -> w.like("supplier_name", keyword.trim()).or().like("supplier_code", keyword.trim()));
        query.orderByDesc("id");
        return ApiResponse.ok(mapper.selectPage(new Page<>(pageNum, pageSize), query));
    }

    @PostMapping
    public ApiResponse<?> create(@Valid @RequestBody Supplier supplier) { if (supplier.getStatus() == null) supplier.setStatus(1); mapper.insert(supplier); return ApiResponse.ok(supplier); }
    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @Valid @RequestBody Supplier supplier) { supplier.setId(id); mapper.updateById(supplier); return ApiResponse.ok(mapper.selectById(id)); }
    @PatchMapping("/{id}/status")
    public ApiResponse<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) { Supplier supplier = new Supplier(); supplier.setId(id); supplier.setStatus(body.get("status")); mapper.updateById(supplier); return ApiResponse.ok(mapper.selectById(id)); }
}
