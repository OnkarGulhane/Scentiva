package com.scentiva.modules.admin.service.impl;

import com.scentiva.modules.admin.dto.*;
import com.scentiva.modules.admin.service.AdminDashboardService;
import com.scentiva.modules.catalog.repository.ProductRepository;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.inventory.repository.InventoryRecordRepository;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.order.repository.OrderItemRepository;
import com.scentiva.modules.order.repository.OrderRepository;
import com.scentiva.modules.returns.model.ReturnStatus;
import com.scentiva.modules.returns.repository.ReturnRepository;
import com.scentiva.modules.review.model.ReviewStatus;
import com.scentiva.modules.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InventoryRecordRepository inventoryRecordRepository;
    private final ReviewRepository reviewRepository;
    private final ReturnRepository returnRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {
        BigDecimal totalRevenue = orderRepository.sumTotalRevenue();
        long totalOrders = orderRepository.countByIsDeletedFalse();
        long totalCustomers = customerRepository.count();
        long totalProducts = productRepository.count();
        long lowStockCount = inventoryRecordRepository.findLowStockRecords().size();
        long pendingReviews = reviewRepository.countByStatusAndIsDeletedFalse(ReviewStatus.PENDING);
        long pendingReturns = returnRepository.countByStatusAndIsDeletedFalse(ReturnStatus.REQUESTED);
        long deliveredOrders = orderRepository.countByStatusAndIsDeletedFalse(OrderStatus.DELIVERED);

        BigDecimal aov = (totalOrders > 0)
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        return DashboardSummaryResponse.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .totalCustomers(totalCustomers)
                .totalProducts(totalProducts)
                .lowStockCount(lowStockCount)
                .pendingReviewsCount(pendingReviews)
                .pendingReturnsCount(pendingReturns)
                .deliveredOrdersCount(deliveredOrders)
                .averageOrderValue(aov)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SalesAnalyticsResponse getSalesAnalytics() {
        BigDecimal totalRevenue = orderRepository.sumTotalRevenue();
        long totalOrders = orderRepository.countByIsDeletedFalse();
        BigDecimal aov = (totalOrders > 0)
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> !o.isDeleted() && o.getStatus() != OrderStatus.CANCELLED && o.getStatus() != OrderStatus.REFUNDED)
                .sorted(Comparator.comparing(Order::getCreatedAt))
                .toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, List<Order>> groupedByMonth = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getCreatedAt().format(formatter), LinkedHashMap::new, Collectors.toList()));

        List<SalesTrendPoint> trends = new ArrayList<>();
        groupedByMonth.forEach((period, periodOrders) -> {
            BigDecimal revenue = periodOrders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            trends.add(SalesTrendPoint.builder()
                    .period(period)
                    .revenue(revenue)
                    .orderCount(periodOrders.size())
                    .build());
        });

        return SalesAnalyticsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .averageOrderValue(aov)
                .trends(trends)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopProductPerformanceDto> getTopSellingProducts(int limit) {
        int queryLimit = (limit <= 0) ? 10 : limit;
        List<Object[]> results = orderItemRepository.findTopSellingProducts(PageRequest.of(0, queryLimit));

        List<TopProductPerformanceDto> topProducts = new ArrayList<>();
        for (Object[] row : results) {
            String name = (String) row[0];
            long qty = (row[1] instanceof Number) ? ((Number) row[1]).longValue() : 0L;
            BigDecimal rev = (row[2] instanceof BigDecimal) ? (BigDecimal) row[2] : BigDecimal.ZERO;

            topProducts.add(TopProductPerformanceDto.builder()
                    .productName(name)
                    .totalQuantitySold(qty)
                    .totalRevenue(rev)
                    .build());
        }

        return topProducts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderStatusCountDto> getOrderStatusBreakdown() {
        List<OrderStatusCountDto> breakdown = new ArrayList<>();
        for (OrderStatus status : OrderStatus.values()) {
            long count = orderRepository.countByStatusAndIsDeletedFalse(status);
            breakdown.add(OrderStatusCountDto.builder()
                    .status(status)
                    .count(count)
                    .build());
        }
        return breakdown;
    }
}
