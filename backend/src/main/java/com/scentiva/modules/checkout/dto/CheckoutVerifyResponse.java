package com.scentiva.modules.checkout.dto;

import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.payment.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutVerifyResponse {
    private String orderNumber;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private boolean success;
    private String message;
}
