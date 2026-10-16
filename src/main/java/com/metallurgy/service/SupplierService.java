package com.metallurgy.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.metallurgy.common.BusinessException;
import com.metallurgy.entity.Supplier;
import com.metallurgy.mapper.SupplierMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {
    private final SupplierMapper mapper;
    public SupplierService(SupplierMapper mapper) { this.mapper = mapper; }

    public Page<Supplier> list(long pageNum, long pageSize, String keyword) {
        QueryWrapper<Supplier> query = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) query.and(w -> w.like("supplier_name", keyword.trim()).or().like("supplier_code", keyword.trim()));
        query.orderByDesc("id");
        return mapper.selectPage(new Page<>(Math.max(pageNum, 1), Math.max(pageSize, 1)), query);
    }

    public Supplier get(Long id) { Supplier supplier = mapper.selectById(id); if (supplier == null) throw new BusinessException(404, "供应商不存在"); return supplier; }

    public Supplier create(Supplier supplier) { ensureCodeUnique(supplier.getSupplierCode(), null); if (supplier.getStatus() == null) supplier.setStatus(1); mapper.insert(supplier); return supplier; }

    public Supplier update(Long id, Supplier supplier) { get(id); ensureCodeUnique(supplier.getSupplierCode(), id); supplier.setId(id); mapper.updateById(supplier); return get(id); }

    public Supplier updateStatus(Long id, Integer status) { Supplier supplier = get(id); if (status == null || (status != 0 && status != 1)) throw new BusinessException(400, "供应商状态无效"); supplier.setStatus(status); mapper.updateById(supplier); return get(id); }

    public void delete(Long id) { get(id); try { mapper.deleteById(id); } catch (DataIntegrityViolationException ex) { throw new BusinessException(409, "供应商已被采购订单引用，无法删除"); } }

    private void ensureCodeUnique(String code, Long id) {
        QueryWrapper<Supplier> query = new QueryWrapper<Supplier>().eq("supplier_code", code);
        if (id != null) query.ne("id", id);
        if (code == null || code.trim().isEmpty() || mapper.selectCount(query) > 0) throw new BusinessException(409, "供应商编码已存在或为空");
    }
}
