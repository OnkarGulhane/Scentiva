package com.scentiva.modules.payment.service;

import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.payment.model.PaymentMethod;
import com.scentiva.modules.payment.model.PaymentProviderType;
import com.scentiva.modules.payment.provider.PaymentInitResult;
import com.scentiva.modules.payment.provider.PaymentVerifyResult;
import com.scentiva.modules.payment.provider.RefundResult;

public interface PaymentService {

    PaymentInitResult processOrderPayment(Order order, PaymentMethod method, PaymentProviderType providerType, String simulatedMode);

    PaymentVerifyResult verifyOrderPayment(Order order, Long paymentId, String gatewayPaymentId, String gatewaySignature, String otp);

    RefundResult processRefund(Order order, Long paymentId, String reason);
}
