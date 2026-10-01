package com.scentiva.modules.payment.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.payment.model.Payment;
import com.scentiva.modules.payment.model.PaymentMethod;
import com.scentiva.modules.payment.model.PaymentProviderType;
import com.scentiva.modules.payment.model.PaymentStatus;
import com.scentiva.modules.payment.model.PaymentTransaction;
import com.scentiva.modules.payment.provider.*;
import com.scentiva.modules.payment.repository.PaymentRepository;
import com.scentiva.modules.payment.repository.PaymentTransactionRepository;
import com.scentiva.modules.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final List<PaymentProvider> paymentProviders;
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    @Transactional
    public PaymentInitResult processOrderPayment(Order order, PaymentMethod method, PaymentProviderType providerType, String simulatedMode) {
        PaymentProviderType resolvedProviderType = providerType != null ? providerType : PaymentProviderType.DEMO;
        PaymentMethod resolvedMethod = method != null ? method : PaymentMethod.CREDIT_CARD;

        PaymentProvider provider = paymentProviders.stream()
                .filter(p -> p.getProviderType() == resolvedProviderType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported payment provider: " + resolvedProviderType));

        PaymentInitCommand command = PaymentInitCommand.builder()
                .orderNumber(order.getOrderNumber())
                .amount(order.getTotalAmount())
                .currency("INR")
                .paymentMethod(resolvedMethod)
                .customerEmail(order.getCustomer().getUser().getEmail())
                .customerPhone(order.getCustomer().getPhone())
                .simulatedMode(simulatedMode)
                .build();

        PaymentInitResult initResult = provider.initializePayment(command);

        Payment payment = Payment.builder()
                .order(order)
                .provider(resolvedProviderType)
                .paymentMethod(resolvedMethod)
                .status(initResult.getStatus())
                .amount(order.getTotalAmount())
                .currency("INR")
                .gatewayOrderId(initResult.getGatewayOrderId())
                .build();

        payment = paymentRepository.save(payment);

        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .transactionReference(initResult.getTransactionReference())
                .gatewayStatus(initResult.getStatus().name())
                .rawResponse(initResult.getRawResponse())
                .build();
        paymentTransactionRepository.save(transaction);

        log.info("Initialized payment id={} for order={}, status={}", payment.getId(), order.getOrderNumber(), payment.getStatus());
        return initResult;
    }

    @Override
    @Transactional
    public PaymentVerifyResult verifyOrderPayment(Order order, Long paymentId, String gatewayPaymentId, String gatewaySignature, String otp) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        PaymentProviderType providerType = payment.getProvider();
        PaymentProvider provider = paymentProviders.stream()
                .filter(p -> p.getProviderType() == providerType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported payment provider: " + providerType));

        PaymentVerifyCommand command = PaymentVerifyCommand.builder()
                .orderNumber(order.getOrderNumber())
                .gatewayOrderId(payment.getGatewayOrderId())
                .gatewayPaymentId(gatewayPaymentId)
                .gatewaySignature(gatewaySignature)
                .otp(otp)
                .expectedAmount(payment.getAmount())
                .build();

        PaymentVerifyResult verifyResult = provider.verifyPayment(command);

        payment.setStatus(verifyResult.getStatus());
        payment = paymentRepository.save(payment);

        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .transactionReference(verifyResult.getTransactionReference())
                .gatewayStatus(verifyResult.getStatus().name())
                .rawResponse(verifyResult.getRawResponse())
                .build();
        paymentTransactionRepository.save(transaction);

        log.info("Verified payment id={} for order={}, isSuccess={}, status={}",
                payment.getId(), order.getOrderNumber(), verifyResult.isSuccess(), payment.getStatus());

        return verifyResult;
    }

    @Override
    @Transactional
    public RefundResult processRefund(Order order, Long paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        PaymentProviderType providerType = payment.getProvider();
        PaymentProvider provider = paymentProviders.stream()
                .filter(p -> p.getProviderType() == providerType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported payment provider: " + providerType));

        RefundCommand command = RefundCommand.builder()
                .orderNumber(order.getOrderNumber())
                .gatewayPaymentId(payment.getGatewayOrderId())
                .refundAmount(payment.getAmount())
                .reason(reason != null ? reason : "Order cancellation")
                .build();

        RefundResult refundResult = provider.processRefund(command);

        if (refundResult.isSuccess()) {
            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);

            PaymentTransaction transaction = PaymentTransaction.builder()
                    .payment(payment)
                    .transactionReference(refundResult.getRefundReference())
                    .gatewayStatus(PaymentStatus.REFUNDED.name())
                    .rawResponse(refundResult.getRawResponse())
                    .build();
            paymentTransactionRepository.save(transaction);

            log.info("Processed refund for payment id={}, order={}, amount={}",
                    payment.getId(), order.getOrderNumber(), refundResult.getRefundedAmount());
        }

        return refundResult;
    }
}
