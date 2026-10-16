package com.metallurgy.service;

import com.metallurgy.common.BusinessException;
import com.metallurgy.entity.Material;
import com.metallurgy.mapper.MaterialMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MaterialServiceTest {
    private MaterialMapper mapper;
    private MaterialService service;

    @BeforeEach
    void setUp() { mapper = mock(MaterialMapper.class); service = new MaterialService(mapper); }

    @Test
    void createRejectsDuplicateCode() {
        when(mapper.selectCount(any())).thenReturn(1L);
        BusinessException error = assertThrows(BusinessException.class, () -> service.create(material("MAT-1", "铁矿粉")));
        assertEquals(409, error.getCode());
        verify(mapper, never()).insert(any());
    }

    @Test
    void updateRejectsMissingMaterial() {
        when(mapper.selectById(9L)).thenReturn(null);
        BusinessException error = assertThrows(BusinessException.class, () -> service.update(9L, material("MAT-9", "铁矿粉")));
        assertEquals(404, error.getCode());
        verify(mapper, never()).updateById(any());
    }

    @Test
    void deleteChecksExistenceBeforeDelegating() {
        when(mapper.selectById(9L)).thenReturn(material("MAT-9", "铁矿粉"));
        service.delete(9L);
        verify(mapper).deleteById(9L);
    }

    private Material material(String code, String name) { Material material = new Material(); material.setMaterialCode(code); material.setMaterialName(name); return material; }
}
