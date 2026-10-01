package com.scentiva.modules.checkout.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutVerifyRequest {

    @NotBlank(message = "Order number is required")
    private String orderNumber;

    private String gatewayPaymentId;
    private String gatewaySignature;
    private String otp;
}
