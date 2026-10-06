package com.scentiva.modules.notification.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface EmailService {

    void sendHtmlEmail(String to, String subject, String htmlContent);

    void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables);

    void sendOrderConfirmationEmail(String to, String customerName, String orderNumber,
                                    BigDecimal totalAmount, String trackingUrl,
                                    List<Map<String, Object>> items, String shippingAddress);

    void sendOrderShippedEmail(String to, String customerName, String orderNumber,
                               String carrierName, String trackingNumber, String trackingUrl);

    void sendOrderDeliveredEmail(String to, String customerName, String orderNumber, String reviewUrl);

    void sendOrderCancelledEmail(String to, String customerName, String orderNumber, BigDecimal refundAmount);

    void sendWelcomeEmail(String to, String customerName, String loyaltyTier);

    void sendPasswordResetEmail(String to, String customerName, String resetToken, String resetUrl);
}
