package com.scentiva.modules.notification.service.impl;

import com.scentiva.modules.notification.provider.EmailProvider;
import com.scentiva.modules.notification.provider.dto.EmailPayload;
import com.scentiva.modules.notification.provider.impl.GmailSmtpEmailProvider;
import com.scentiva.modules.notification.provider.impl.MockEmailProvider;
import com.scentiva.modules.notification.provider.impl.ResendEmailProvider;
import com.scentiva.modules.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScentivaEmailServiceImpl implements EmailService {

    private final ResendEmailProvider resendProvider;
    private final GmailSmtpEmailProvider gmailProvider;
    private final MockEmailProvider mockProvider;
    private final TemplateEngine templateEngine;

    @Value("${scentiva.mail.provider:resend}")
    private String preferredProvider;

    @Value("${scentiva.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${scentiva.mail.from-email:SCENTIVA <orders@scentiva.com>}")
    private String fromEmail;

    @Value("${scentiva.app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (!mailEnabled) {
            log.info("[EmailService] Mail dispatch disabled via configuration. Target: {}", to);
            return;
        }

        EmailPayload payload = EmailPayload.builder()
                .to(to)
                .subject(subject)
                .htmlContent(htmlContent)
                .fromName("SCENTIVA Haute Parfumerie")
                .fromEmail(fromEmail)
                .build();

        dispatchWithFallback(payload);
    }

    @Override
    @Async
    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        if (!mailEnabled) {
            log.info("[EmailService] Mail dispatch disabled via configuration. Target: {}", to);
            return;
        }

        try {
            Context context = new Context();
            if (variables != null) {
                context.setVariables(variables);
            }
            context.setVariable("frontendUrl", frontendUrl);

            String htmlContent = templateEngine.process("email/" + templateName, context);
            sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception ex) {
            log.error("[EmailService] Failed to render template 'email/{}' for {}: {}", templateName, to, ex.getMessage(), ex);
        }
    }

    @Override
    @Async
    public void sendOrderConfirmationEmail(String to, String customerName, String orderNumber,
                                           BigDecimal totalAmount, String trackingUrl,
                                           List<Map<String, Object>> items, String shippingAddress) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("customerName", customerName != null ? customerName : "Valued Customer");
        vars.put("orderNumber", orderNumber);
        vars.put("totalAmount", totalAmount != null ? totalAmount.toPlainString() : "0.00");
        vars.put("trackingUrl", trackingUrl != null ? trackingUrl : frontendUrl + "/orders/" + orderNumber);
        vars.put("items", items != null ? items : List.of());
        vars.put("shippingAddress", shippingAddress != null ? shippingAddress : "Shipping Address Provided");

        String subject = "Order Confirmed: " + orderNumber + " • SCENTIVA Haute Parfumerie";
        sendTemplateEmail(to, subject, "order-confirmed", vars);
    }

    @Override
    @Async
    public void sendOrderShippedEmail(String to, String customerName, String orderNumber,
                                      String carrierName, String trackingNumber, String trackingUrl) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("customerName", customerName != null ? customerName : "Valued Customer");
        vars.put("orderNumber", orderNumber);
        vars.put("carrierName", carrierName != null ? carrierName : "Luxury Express Courier");
        vars.put("trackingNumber", trackingNumber != null ? trackingNumber : "SC-TRK-PENDING");
        vars.put("trackingUrl", trackingUrl != null ? trackingUrl : frontendUrl + "/orders/" + orderNumber);

        String subject = "Dispatched: Your SCENTIVA Flacon is En Route (" + orderNumber + ")";
        sendTemplateEmail(to, subject, "order-shipped", vars);
    }

    @Override
    @Async
    public void sendOrderDeliveredEmail(String to, String customerName, String orderNumber, String reviewUrl) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("customerName", customerName != null ? customerName : "Valued Customer");
        vars.put("orderNumber", orderNumber);
        vars.put("reviewUrl", reviewUrl != null ? reviewUrl : frontendUrl + "/account");

        String subject = "Delivered: Experience Your SCENTIVA Fragrance (" + orderNumber + ")";
        sendTemplateEmail(to, subject, "order-delivered", vars);
    }

    @Override
    @Async
    public void sendOrderCancelledEmail(String to, String customerName, String orderNumber, BigDecimal refundAmount) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("customerName", customerName != null ? customerName : "Valued Customer");
        vars.put("orderNumber", orderNumber);
        vars.put("refundAmount", refundAmount != null ? refundAmount.toPlainString() : "0.00");

        String subject = "Order Cancelled: " + orderNumber + " • Refund Confirmation";
        sendTemplateEmail(to, subject, "order-cancelled", vars);
    }

    @Override
    @Async
    public void sendWelcomeEmail(String to, String customerName, String loyaltyTier) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("customerName", customerName != null ? customerName : "Connoisseur");
        vars.put("loyaltyTier", loyaltyTier != null ? loyaltyTier : "BRONZE");
        vars.put("portalUrl", frontendUrl + "/account");

        String subject = "Welcome to SCENTIVA Privé Club • Haute Parfumerie";
        sendTemplateEmail(to, subject, "welcome", vars);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String customerName, String resetToken, String resetUrl) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("customerName", customerName != null ? customerName : "User");
        vars.put("resetToken", resetToken);
        vars.put("resetUrl", resetUrl != null ? resetUrl : frontendUrl + "/auth/reset-password?token=" + resetToken);

        String subject = "Reset Your SCENTIVA Password";
        sendTemplateEmail(to, subject, "password-reset", vars);
    }

    private void dispatchWithFallback(EmailPayload payload) {
        boolean sent = false;

        // 1. If Resend preferred
        if ("resend".equalsIgnoreCase(preferredProvider)) {
            if (resendProvider.isAvailable()) {
                sent = resendProvider.sendEmail(payload);
            }
            if (!sent && gmailProvider.isAvailable()) {
                log.info("[EmailService] Primary provider 'resend' failed or unavailable. Falling back to Gmail SMTP...");
                sent = gmailProvider.sendEmail(payload);
            }
        }
        // 2. If Gmail SMTP preferred
        else if ("gmail".equalsIgnoreCase(preferredProvider)) {
            if (gmailProvider.isAvailable()) {
                sent = gmailProvider.sendEmail(payload);
            }
            if (!sent && resendProvider.isAvailable()) {
                log.info("[EmailService] Primary provider 'gmail' failed or unavailable. Falling back to Resend...");
                sent = resendProvider.sendEmail(payload);
            }
        }

        // 3. Fallback to mock if neither succeeded or in mock mode
        if (!sent) {
            log.info("[EmailService] Live providers unconfigured or failed. Recording in MockEmailProvider.");
            mockProvider.sendEmail(payload);
        }
    }
}
