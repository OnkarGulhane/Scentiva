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
public class RefundCommand {
    private String orderNumber;
    private String gatewayPaymentId;
    private BigDecimal refundAmount;
    private String reason;
}
