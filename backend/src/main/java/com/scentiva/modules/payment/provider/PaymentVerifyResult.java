package com.scentiva.modules.payment.provider;

import com.scentiva.modules.payment.model.PaymentProviderType;
import com.scentiva.modules.payment.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerifyResult {
    private PaymentProviderType provider;
    private boolean isSuccess;
    private PaymentStatus status;
    private String transactionReference;
    private String rawResponse;
    private String message;
}
