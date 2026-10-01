package com.scentiva.modules.order.service;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.order.dto.*;
import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.shipping.dto.ShipmentTrackingResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    ApiPaginatedResponse<OrderSummaryResponse> getCustomerOrders(String email, Pageable pageable);

    OrderResponse getOrderByNumber(String email, String orderNumber);

    OrderResponse cancelOrder(String email, String orderNumber, OrderCancelRequest request);

    OrderResponse updateOrderStatus(String orderNumber, OrderStatusUpdateRequest request);

    ApiPaginatedResponse<OrderSummaryResponse> getAllOrders(OrderStatus status, Pageable pageable);

    ShipmentTrackingResponse trackOrder(String email, String orderNumber);
}
