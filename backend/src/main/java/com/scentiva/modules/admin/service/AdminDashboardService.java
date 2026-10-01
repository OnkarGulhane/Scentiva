package com.scentiva.modules.admin.service;

import com.scentiva.modules.admin.dto.DashboardSummaryResponse;
import com.scentiva.modules.admin.dto.OrderStatusCountDto;
import com.scentiva.modules.admin.dto.SalesAnalyticsResponse;
import com.scentiva.modules.admin.dto.TopProductPerformanceDto;

import java.util.List;

public interface AdminDashboardService {

    DashboardSummaryResponse getDashboardSummary();

    SalesAnalyticsResponse getSalesAnalytics();

    List<TopProductPerformanceDto> getTopSellingProducts(int limit);

    List<OrderStatusCountDto> getOrderStatusBreakdown();
}
