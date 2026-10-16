package com.metallurgy.controller;

import com.metallurgy.common.ApiResponse;
import com.metallurgy.entity.Material;
import com.metallurgy.service.MaterialService;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {
    private final MaterialService service;
    public MaterialController(MaterialService service) { this.service = service; }

    @GetMapping
    public ApiResponse<?> list(@RequestParam(defaultValue = "1") long pageNum, @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.list(pageNum, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<?> get(@PathVariable Long id) { return ApiResponse.ok(service.get(id)); }

    @PostMapping
    public ApiResponse<?> create(@Valid @RequestBody Material material) { return ApiResponse.ok(service.create(material)); }
    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @Valid @RequestBody Material material) { return ApiResponse.ok(service.update(id, material)); }
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) { service.delete(id); return ApiResponse.ok(null); }
}
