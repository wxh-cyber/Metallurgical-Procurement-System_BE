package com.metallurgy.service;

import com.metallurgy.common.BusinessException;
import com.metallurgy.entity.Supplier;
import com.metallurgy.mapper.SupplierMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SupplierServiceTest {
    private SupplierMapper mapper;
    private SupplierService service;

    @BeforeEach
    void setUp() { mapper = mock(SupplierMapper.class); service = new SupplierService(mapper); }

    @Test
    void createRejectsDuplicateCode() {
        when(mapper.selectCount(any())).thenReturn(1L);
        BusinessException error = assertThrows(BusinessException.class, () -> service.create(supplier("SUP-1")));
        assertEquals(409, error.getCode());
        verify(mapper, never()).insert(any());
    }

    @Test
    void updateStatusRejectsMissingSupplier() {
        when(mapper.selectById(7L)).thenReturn(null);
        BusinessException error = assertThrows(BusinessException.class, () -> service.updateStatus(7L, 1));
        assertEquals(404, error.getCode());
        verify(mapper, never()).updateById(any());
    }

    @Test
    void deleteDelegatesToMapperAfterExistenceCheck() {
        when(mapper.selectById(7L)).thenReturn(supplier("SUP-7"));
        service.delete(7L);
        verify(mapper).deleteById(7L);
    }

    private Supplier supplier(String code) { Supplier supplier = new Supplier(); supplier.setSupplierCode(code); supplier.setSupplierName("供应商"); return supplier; }
}
