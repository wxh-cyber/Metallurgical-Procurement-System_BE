package com.metallurgy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.metallurgy.common.ApiResponse;
import com.metallurgy.entity.Material;
import com.metallurgy.mapper.MaterialMapper;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {
    private final MaterialMapper mapper;
    public MaterialController(MaterialMapper mapper) { this.mapper = mapper; }

    @GetMapping
    public ApiResponse<?> list(@RequestParam(defaultValue = "1") long pageNum, @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) String keyword) {
        QueryWrapper<Material> query = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) query.and(w -> w.like("material_name", keyword.trim()).or().like("material_code", keyword.trim()));
        query.orderByDesc("id");
        return ApiResponse.ok(mapper.selectPage(new Page<>(pageNum, pageSize), query));
    }

    @PostMapping
    public ApiResponse<?> create(@Valid @RequestBody Material material) { if (material.getStatus() == null) material.setStatus(1); mapper.insert(material); return ApiResponse.ok(material); }
    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @Valid @RequestBody Material material) { material.setId(id); mapper.updateById(material); return ApiResponse.ok(mapper.selectById(id)); }
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) { mapper.deleteById(id); return ApiResponse.ok(null); }
}
