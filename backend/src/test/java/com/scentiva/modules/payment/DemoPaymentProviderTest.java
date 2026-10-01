package com.scentiva.modules.payment;

import com.scentiva.modules.payment.model.PaymentMethod;
import com.scentiva.modules.payment.model.PaymentProviderType;
import com.scentiva.modules.payment.model.PaymentStatus;
import com.scentiva.modules.payment.provider.*;
import com.scentiva.modules.payment.provider.impl.DemoPaymentProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DemoPaymentProviderTest {

    private DemoPaymentProvider demoPaymentProvider;

    @BeforeEach
    void setUp() {
        demoPaymentProvider = new DemoPaymentProvider();
    }

    @Test
    @DisplayName("Should initialize instant success payment by default")
    void shouldInitializeSuccessPayment() {
        PaymentInitCommand command = PaymentInitCommand.builder()
                .orderNumber("SC-2026-TEST01")
                .amount(new BigDecimal("1500.00"))
                .currency("INR")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .simulatedMode("SUCCESS")
                .build();

        PaymentInitResult result = demoPaymentProvider.initializePayment(command);

        assertThat(result.getProvider()).isEqualTo(PaymentProviderType.DEMO);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(result.isRequiresAction()).isFalse();
        assertThat(result.getGatewayOrderId()).isNotBlank();
        assertThat(result.getTransactionReference()).isNotBlank();
    }

    @Test
    @DisplayName("Should initialize payment requiring 3D-Secure action when requested")
    void shouldInitializeRequiresActionPayment() {
        PaymentInitCommand command = PaymentInitCommand.builder()
                .orderNumber("SC-2026-TEST02")
                .amount(new BigDecimal("2500.00"))
                .currency("INR")
                .paymentMethod(PaymentMethod.NET_BANKING)
                .simulatedMode("REQUIRES_ACTION")
                .build();

        PaymentInitResult result = demoPaymentProvider.initializePayment(command);

        assertThat(result.getProvider()).isEqualTo(PaymentProviderType.DEMO);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(result.isRequiresAction()).isTrue();
        assertThat(result.getActionUrl()).isNotBlank();
    }

    @Test
    @DisplayName("Should handle simulated payment decline")
    void shouldHandleSimulatedDecline() {
        PaymentInitCommand command = PaymentInitCommand.builder()
                .orderNumber("SC-2026-TEST03")
                .amount(new BigDecimal("999.00"))
                .currency("INR")
                .paymentMethod(PaymentMethod.DEBIT_CARD)
                .simulatedMode("DECLINE")
                .build();

        PaymentInitResult result = demoPaymentProvider.initializePayment(command);

        assertThat(result.getProvider()).isEqualTo(PaymentProviderType.DEMO);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(result.getMessage()).contains("declined");
    }

    @Test
    @DisplayName("Should verify payment successfully with valid OTP")
    void shouldVerifyPaymentSuccess() {
        PaymentVerifyCommand command = PaymentVerifyCommand.builder()
                .orderNumber("SC-2026-TEST01")
                .gatewayPaymentId("PAY-123456")
                .otp("123456")
                .build();

        PaymentVerifyResult result = demoPaymentProvider.verifyPayment(command);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(result.getTransactionReference()).isEqualTo("PAY-123456");
    }

    @Test
    @DisplayName("Should fail payment verification when invalid OTP provided")
    void shouldFailVerificationWithInvalidOtp() {
        PaymentVerifyCommand command = PaymentVerifyCommand.builder()
                .orderNumber("SC-2026-TEST01")
                .gatewayPaymentId("PAY-123456")
                .otp("INVALID")
                .build();

        PaymentVerifyResult result = demoPaymentProvider.verifyPayment(command);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(result.getMessage()).contains("Invalid OTP");
    }

    @Test
    @DisplayName("Should process refund successfully in demo mode")
    void shouldProcessRefund() {
        RefundCommand command = RefundCommand.builder()
                .orderNumber("SC-2026-TEST01")
                .gatewayPaymentId("PAY-123456")
                .refundAmount(new BigDecimal("500.00"))
                .reason("Customer cancellation")
                .build();

        RefundResult result = demoPaymentProvider.processRefund(command);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getRefundReference()).isNotBlank();
        assertThat(result.getRefundedAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
    }
}
