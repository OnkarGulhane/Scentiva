package com.scentiva.modules.order.dto;

import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.payment.model.PaymentMethod;
import com.scentiva.modules.payment.model.PaymentStatus;
import com.scentiva.modules.shipping.dto.ShipmentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerEmail;
    private String customerName;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal deliveryFee;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String notes;
    private List<OrderItemResponse> items;
    private String customerSnapshotJson;
    private String shippingAddressSnapshotJson;
    private String pricingMatrixSnapshotJson;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private ShipmentResponse shipment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
