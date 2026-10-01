package com.scentiva.modules.checkout.service;

import com.scentiva.modules.checkout.dto.*;

public interface CheckoutService {

    CheckoutSummaryResponse reviewCheckout(String email, Long shippingAddressId, String couponCode);

    CheckoutProcessResponse processCheckout(String email, String idempotencyKey, CheckoutProcessRequest request);

    CheckoutVerifyResponse verifyCheckout(String email, CheckoutVerifyRequest request);
}
