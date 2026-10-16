package com.metallurgy.controller;

import com.metallurgy.mapper.PurchaseReportMapper;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class ReportControllerTest {
    @Test
    void purchaseReturnsRequestedYearAndEmptyRows() {
        PurchaseReportMapper mapper = mock(PurchaseReportMapper.class);
        when(mapper.byYear(2025)).thenReturn(Collections.emptyList());
        ReportController controller = new ReportController(mapper);
        Map<?, ?> data = (Map<?, ?>) controller.purchase(2025).getData();
        assertEquals(2025, data.get("year"));
        assertEquals(Collections.emptyList(), data.get("byMaterial"));
        verify(mapper).byYear(2025);
    }

    @Test
    void purchaseUsesCurrentYearWhenYearMissing() {
        PurchaseReportMapper mapper = mock(PurchaseReportMapper.class);
        when(mapper.byYear(anyInt())).thenReturn(Collections.emptyList());
        ReportController controller = new ReportController(mapper);
        assertEquals(java.time.Year.now().getValue(), ((Map<?, ?>) controller.purchase(null).getData()).get("year"));
    }
}
