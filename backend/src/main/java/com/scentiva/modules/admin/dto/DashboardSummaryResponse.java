package com.scentiva.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long totalCustomers;
    private long totalProducts;
    private long lowStockCount;
    private long pendingReviewsCount;
    private long pendingReturnsCount;
    private long deliveredOrdersCount;
    private BigDecimal averageOrderValue;
}
