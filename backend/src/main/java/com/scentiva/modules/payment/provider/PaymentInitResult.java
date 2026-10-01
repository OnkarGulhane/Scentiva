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
public class PaymentInitResult {
    private PaymentProviderType provider;
    private String gatewayOrderId;
    private PaymentStatus status;
    private boolean requiresAction;
    private String actionUrl;
    private String transactionReference;
    private String rawResponse;
    private String message;
}
