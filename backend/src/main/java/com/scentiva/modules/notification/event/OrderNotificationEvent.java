package com.scentiva.modules.notification.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationEvent {
    private Long customerId;
    private String customerEmail;
    private String customerName;
    private String orderNumber;
    private String eventType; // "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED", "REFUNDED"
    private BigDecimal totalAmount;
    private String trackingNumber;
    private String carrierName;
}
