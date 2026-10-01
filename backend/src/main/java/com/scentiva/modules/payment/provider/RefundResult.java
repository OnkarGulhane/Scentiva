package com.scentiva.modules.payment.provider;

import com.scentiva.modules.payment.model.PaymentProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResult {
    private PaymentProviderType provider;
    private boolean isSuccess;
    private String refundReference;
    private BigDecimal refundedAmount;
    private String rawResponse;
    private String message;
}
