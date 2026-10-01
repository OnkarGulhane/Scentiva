package com.scentiva.modules.payment.provider;

import com.scentiva.modules.payment.model.PaymentProviderType;
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
public class PaymentVerifyCommand {
    private String orderNumber;
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String gatewaySignature;
    private String otp;
    private BigDecimal expectedAmount;
}
