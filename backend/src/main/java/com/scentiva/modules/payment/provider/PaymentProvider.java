package com.scentiva.modules.payment.provider;

import com.scentiva.modules.payment.model.PaymentProviderType;

public interface PaymentProvider {

    PaymentProviderType getProviderType();

    PaymentInitResult initializePayment(PaymentInitCommand command);

    PaymentVerifyResult verifyPayment(PaymentVerifyCommand command);

    RefundResult processRefund(RefundCommand command);
}
