package com.metallurgy.controller;

import com.metallurgy.common.ApiResponse;
import com.metallurgy.mapper.PurchaseReportMapper;
import org.springframework.web.bind.annotation.*;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final PurchaseReportMapper mapper;
    public ReportController(PurchaseReportMapper mapper) { this.mapper = mapper; }
    @GetMapping("/purchase")
    public ApiResponse<?> purchase(@RequestParam(required = false) Integer year) {
        int reportYear = year == null ? Year.now().getValue() : year;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("year", reportYear);
        result.put("byMaterial", mapper.byYear(reportYear));
        return ApiResponse.ok(result);
    }
}
