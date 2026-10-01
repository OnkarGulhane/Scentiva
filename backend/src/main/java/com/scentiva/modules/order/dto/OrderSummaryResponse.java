package com.scentiva.modules.order.dto;

import com.scentiva.modules.order.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private int totalItems;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private String trackingNumber;
    private String carrierName;
}
