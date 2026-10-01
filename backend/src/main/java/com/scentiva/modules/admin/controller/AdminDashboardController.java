package com.scentiva.modules.admin.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.admin.dto.DashboardSummaryResponse;
import com.scentiva.modules.admin.dto.OrderStatusCountDto;
import com.scentiva.modules.admin.dto.SalesAnalyticsResponse;
import com.scentiva.modules.admin.dto.TopProductPerformanceDto;
import com.scentiva.modules.admin.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'MANAGER', 'ROLE_PRODUCT_MANAGER', 'ROLE_MARKETING_MANAGER', 'PRODUCT_MANAGER', 'MARKETING_MANAGER')")
@Tag(name = "Admin Dashboard & Analytics", description = "Overview metrics, revenue telemetry, top fragrance performance and order status breakdown")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary KPIs", description = "Retrieves top-level store revenue, total orders, customers, inventory alerts and pending items.")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary() {
        DashboardSummaryResponse summary = adminDashboardService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.ok(summary, "Dashboard summary retrieved"));
    }

    @GetMapping("/sales-analytics")
    @Operation(summary = "Get sales trends & analytics", description = "Returns historical revenue aggregation and order volume time-series.")
    public ResponseEntity<ApiResponse<SalesAnalyticsResponse>> getSalesAnalytics() {
        SalesAnalyticsResponse analytics = adminDashboardService.getSalesAnalytics();
        return ResponseEntity.ok(ApiResponse.ok(analytics, "Sales analytics retrieved"));
    }

    @GetMapping("/top-products")
    @Operation(summary = "Get top performing perfumes", description = "Ranks catalog perfumes by sales volume and generated revenue.")
    public ResponseEntity<ApiResponse<List<TopProductPerformanceDto>>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<TopProductPerformanceDto> topProducts = adminDashboardService.getTopSellingProducts(limit);
        return ResponseEntity.ok(ApiResponse.ok(topProducts, "Top products retrieved"));
    }

    @GetMapping("/order-status-breakdown")
    @Operation(summary = "Get order counts by status", description = "Returns current distribution of orders across lifecycle states.")
    public ResponseEntity<ApiResponse<List<OrderStatusCountDto>>> getOrderStatusBreakdown() {
        List<OrderStatusCountDto> breakdown = adminDashboardService.getOrderStatusBreakdown();
        return ResponseEntity.ok(ApiResponse.ok(breakdown, "Order status breakdown retrieved"));
    }
}
