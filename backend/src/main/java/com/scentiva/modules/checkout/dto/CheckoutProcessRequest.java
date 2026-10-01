package com.scentiva.modules.checkout.dto;

import com.scentiva.modules.payment.model.PaymentMethod;
import com.scentiva.modules.payment.model.PaymentProviderType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutProcessRequest {

    @NotNull(message = "Shipping address ID is required")
    private Long shippingAddressId;

    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

    @Builder.Default
    private PaymentProviderType paymentProvider = PaymentProviderType.DEMO;

    private String couponCode;

    private String notes;

    private String simulatedMode; // e.g., "SUCCESS", "REQUIRES_ACTION", "DECLINE"
}
