package com.example.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class DashboardResponse {
    private Map<String, BigDecimal> monthlyTotalsByCategory;
    private List<VendorTotal> topVendors;
    private long anomalyCount;
    private List<Long> anomalyIds;

    @Data
    @AllArgsConstructor
    public static class VendorTotal {
        private String vendor;
        private BigDecimal total;
    }
}
