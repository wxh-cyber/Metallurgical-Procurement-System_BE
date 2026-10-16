package com.metallurgy.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.metallurgy.common.BusinessException;
import com.metallurgy.entity.Material;
import com.metallurgy.mapper.MaterialMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class MaterialService {
    private final MaterialMapper mapper;
    public MaterialService(MaterialMapper mapper) { this.mapper = mapper; }

    public Page<Material> list(long pageNum, long pageSize, String keyword) {
        QueryWrapper<Material> query = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) query.and(w -> w.like("material_name", keyword.trim()).or().like("material_code", keyword.trim()));
        query.orderByDesc("id");
        return mapper.selectPage(new Page<>(Math.max(pageNum, 1), Math.max(pageSize, 1)), query);
    }

    public Material get(Long id) { Material material = mapper.selectById(id); if (material == null) throw new BusinessException(404, "物料不存在"); return material; }

    public Material create(Material material) { ensureCodeUnique(material.getMaterialCode(), null); if (material.getStatus() == null) material.setStatus(1); mapper.insert(material); return material; }

    public Material update(Long id, Material material) { get(id); ensureCodeUnique(material.getMaterialCode(), id); material.setId(id); mapper.updateById(material); return get(id); }

    public void delete(Long id) { get(id); try { mapper.deleteById(id); } catch (DataIntegrityViolationException ex) { throw new BusinessException(409, "物料已被采购或库存记录引用，无法删除"); } }

    private void ensureCodeUnique(String code, Long id) {
        QueryWrapper<Material> query = new QueryWrapper<Material>().eq("material_code", code);
        if (id != null) query.ne("id", id);
        if (code == null || code.trim().isEmpty() || mapper.selectCount(query) > 0) throw new BusinessException(409, "物料编码已存在或为空");
    }
}
