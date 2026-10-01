package com.scentiva.modules.payment.provider;

import com.scentiva.modules.payment.model.PaymentMethod;
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
public class PaymentInitCommand {
    private String orderNumber;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private String customerEmail;
    private String customerPhone;
    private String simulatedMode; // "SUCCESS", "REQUIRES_ACTION", "DECLINE"
}
