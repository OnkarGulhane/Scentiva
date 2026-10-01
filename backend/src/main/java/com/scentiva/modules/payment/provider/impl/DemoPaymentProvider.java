package com.scentiva.modules.payment.provider.impl;

import com.scentiva.modules.payment.model.PaymentProviderType;
import com.scentiva.modules.payment.model.PaymentStatus;
import com.scentiva.modules.payment.provider.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class DemoPaymentProvider implements PaymentProvider {

    @Override
    public PaymentProviderType getProviderType() {
        return PaymentProviderType.DEMO;
    }

    @Override
    public PaymentInitResult initializePayment(PaymentInitCommand command) {
        String gatewayOrderId = "DEMO-GW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String txRef = "TX-DEMO-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        String mode = command.getSimulatedMode() != null ? command.getSimulatedMode().toUpperCase() : "SUCCESS";

        if ("DECLINE".equals(mode) || "FAIL".equals(mode)) {
            log.info("Demo payment initialized with simulated failure for order {}", command.getOrderNumber());
            return PaymentInitResult.builder()
                    .provider(PaymentProviderType.DEMO)
                    .gatewayOrderId(gatewayOrderId)
                    .status(PaymentStatus.FAILED)
                    .requiresAction(false)
                    .transactionReference(txRef)
                    .rawResponse("{\"status\":\"DECLINED\",\"reason\":\"Simulated card decline\"}")
                    .message("Payment declined by issuing bank (Demo Mode)")
                    .build();
        }

        if ("REQUIRES_ACTION".equals(mode)) {
            log.info("Demo payment initialized requiring 3DS action for order {}", command.getOrderNumber());
            return PaymentInitResult.builder()
                    .provider(PaymentProviderType.DEMO)
                    .gatewayOrderId(gatewayOrderId)
                    .status(PaymentStatus.PENDING)
                    .requiresAction(true)
                    .actionUrl("/checkout/payment?session=" + gatewayOrderId)
                    .transactionReference(txRef)
                    .rawResponse("{\"status\":\"REQUIRES_3DS_ACTION\",\"otpRequired\":true}")
                    .message("Payment requires 3D-Secure authentication (Demo Mode)")
                    .build();
        }

        log.info("Demo payment initialized successfully for order {}, amount={}", command.getOrderNumber(), command.getAmount());
        return PaymentInitResult.builder()
                .provider(PaymentProviderType.DEMO)
                .gatewayOrderId(gatewayOrderId)
                .status(PaymentStatus.SUCCESS)
                .requiresAction(false)
                .transactionReference(txRef)
                .rawResponse("{\"status\":\"AUTHORIZED_AND_CAPTURED\",\"amount\":" + command.getAmount() + "}")
                .message("Payment authorized and captured instantly (Demo Mode)")
                .build();
    }

    @Override
    public PaymentVerifyResult verifyPayment(PaymentVerifyCommand command) {
        String txRef = command.getGatewayPaymentId() != null
                ? command.getGatewayPaymentId()
                : "TX-VERIFY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        if ("INVALID".equalsIgnoreCase(command.getOtp()) || "FAIL".equalsIgnoreCase(command.getGatewayPaymentId())) {
            log.warn("Demo payment verification failed for order {}", command.getOrderNumber());
            return PaymentVerifyResult.builder()
                    .provider(PaymentProviderType.DEMO)
                    .isSuccess(false)
                    .status(PaymentStatus.FAILED)
                    .transactionReference(txRef)
                    .rawResponse("{\"status\":\"FAILED\",\"reason\":\"Invalid OTP verification code\"}")
                    .message("Payment verification failed: Invalid OTP")
                    .build();
        }

        log.info("Demo payment verification succeeded for order {}", command.getOrderNumber());
        return PaymentVerifyResult.builder()
                .provider(PaymentProviderType.DEMO)
                .isSuccess(true)
                .status(PaymentStatus.SUCCESS)
                .transactionReference(txRef)
                .rawResponse("{\"status\":\"VERIFIED_AND_SETTLED\"}")
                .message("Payment successfully verified and settled")
                .build();
    }

    @Override
    public RefundResult processRefund(RefundCommand command) {
        String refundRef = "REF-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        log.info("Processed demo refund for order {}, amount={}", command.getOrderNumber(), command.getRefundAmount());

        return RefundResult.builder()
                .provider(PaymentProviderType.DEMO)
                .isSuccess(true)
                .refundReference(refundRef)
                .refundedAmount(command.getRefundAmount())
                .rawResponse("{\"status\":\"REFUND_SETTLED\"}")
                .message("Refund processed successfully (Demo Mode)")
                .build();
    }
}
