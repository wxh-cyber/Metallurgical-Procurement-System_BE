package com.metallurgy.controller;

import com.metallurgy.common.ApiResponse;
import com.metallurgy.entity.Supplier;
import com.metallurgy.service.SupplierService;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierService service;
    public SupplierController(SupplierService service) { this.service = service; }

    @GetMapping
    public ApiResponse<?> list(@RequestParam(defaultValue = "1") long pageNum, @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.list(pageNum, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<?> get(@PathVariable Long id) { return ApiResponse.ok(service.get(id)); }

    @PostMapping
    public ApiResponse<?> create(@Valid @RequestBody Supplier supplier) { return ApiResponse.ok(service.create(supplier)); }
    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @Valid @RequestBody Supplier supplier) { return ApiResponse.ok(service.update(id, supplier)); }
    @PatchMapping("/{id}/status")
    public ApiResponse<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) { return ApiResponse.ok(service.updateStatus(id, body.get("status"))); }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) { service.delete(id); return ApiResponse.ok(null); }
}
