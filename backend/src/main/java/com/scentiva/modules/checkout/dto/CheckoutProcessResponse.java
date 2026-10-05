package com.scentiva.modules.checkout.dto;

import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.payment.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutProcessResponse {
    private Long orderId;
    private String orderNumber;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private Long paymentId;
    private PaymentStatus paymentStatus;
    private String gatewayOrderId;
    private String keyId;
    private boolean requiresAction;
    private String actionUrl;
    private String message;
}
