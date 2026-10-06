package com.scentiva.modules.notification.service;

import com.scentiva.modules.notification.provider.impl.MockEmailProvider;
import com.scentiva.modules.notification.provider.impl.ResendEmailProvider;
import com.scentiva.modules.notification.provider.impl.GmailSmtpEmailProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private MockEmailProvider mockEmailProvider;

    @Autowired
    private ResendEmailProvider resendEmailProvider;

    @Autowired
    private GmailSmtpEmailProvider gmailSmtpEmailProvider;

    @BeforeEach
    void setUp() {
        mockEmailProvider.clear();
    }

    @Test
    @DisplayName("Should render and dispatch Order Confirmation email template")
    void testSendOrderConfirmationEmail() throws InterruptedException {
        String recipient = "omkar.test@scentiva.luxury";
        String customerName = "Omkar Gulhane";
        String orderNumber = "SC-2026-TEST-001";
        BigDecimal amount = new BigDecimal("14950.00");
        String address = "Aura Towers, Baner, Pune 411045";

        List<Map<String, Object>> items = List.of(
                Map.of("productName", "Baccarat Rouge 540", "volume", "70ml", "quantity", 1, "price", "14,950.00")
        );

        emailService.sendOrderConfirmationEmail(recipient, customerName, orderNumber, amount, "https://scentiva.luxury/orders/" + orderNumber, items, address);

        // Wait brief moment for async execution
        Thread.sleep(200);

        assertThat(mockEmailProvider.getSentEmails()).isNotEmpty();
        var sent = mockEmailProvider.getSentEmails().get(0);
        assertThat(sent.getTo()).isEqualTo(recipient);
        assertThat(sent.getSubject()).contains("Order Confirmed");
        assertThat(sent.getSubject()).contains(orderNumber);
        assertThat(sent.getHtmlContent()).contains("SCENTIVA");
        assertThat(sent.getHtmlContent()).contains("Omkar Gulhane");
        assertThat(sent.getHtmlContent()).contains("14950.00");
    }

    @Test
    @DisplayName("Should render and dispatch Order Shipped email template with tracking")
    void testSendOrderShippedEmail() throws InterruptedException {
        String recipient = "client@scentiva.luxury";
        emailService.sendOrderShippedEmail(recipient, "Aria", "SC-2026-0099", "Blue Dart Apex", "SC-TRK-88123", "https://scentiva.luxury/track");

        Thread.sleep(200);

        assertThat(mockEmailProvider.getSentEmails()).isNotEmpty();
        var sent = mockEmailProvider.getSentEmails().get(0);
        assertThat(sent.getTo()).isEqualTo(recipient);
        assertThat(sent.getSubject()).contains("Dispatched");
        assertThat(sent.getHtmlContent()).contains("SC-TRK-88123");
        assertThat(sent.getHtmlContent()).contains("Blue Dart Apex");
    }

    @Test
    @DisplayName("Should render and dispatch Welcome email with loyalty tier")
    void testSendWelcomeEmail() throws InterruptedException {
        String recipient = "newmember@scentiva.luxury";
        emailService.sendWelcomeEmail(recipient, "Vikram", "VIP");

        Thread.sleep(200);

        assertThat(mockEmailProvider.getSentEmails()).isNotEmpty();
        var sent = mockEmailProvider.getSentEmails().get(0);
        assertThat(sent.getTo()).isEqualTo(recipient);
        assertThat(sent.getSubject()).contains("Welcome to SCENTIVA Privé Club");
        assertThat(sent.getHtmlContent()).contains("VIP");
        assertThat(sent.getHtmlContent()).contains("Vikram");
    }

    @Test
    @DisplayName("Should render and dispatch Password Reset email")
    void testSendPasswordResetEmail() throws InterruptedException {
        String recipient = "security@scentiva.luxury";
        emailService.sendPasswordResetEmail(recipient, "Vikram", "token-xyz-123", "https://scentiva.luxury/auth/reset?token=xyz");

        Thread.sleep(200);

        assertThat(mockEmailProvider.getSentEmails()).isNotEmpty();
        var sent = mockEmailProvider.getSentEmails().get(0);
        assertThat(sent.getTo()).isEqualTo(recipient);
        assertThat(sent.getSubject()).contains("Reset Your SCENTIVA Password");
        assertThat(sent.getHtmlContent()).contains("https://scentiva.luxury/auth/reset?token=xyz");
    }

    @Test
    @DisplayName("Should verify provider names and availability indicators")
    void testProviderIdentities() {
        assertThat(mockEmailProvider.getProviderName()).isEqualTo("MOCK");
        assertThat(mockEmailProvider.isAvailable()).isTrue();

        assertThat(resendEmailProvider.getProviderName()).isEqualTo("RESEND");
        assertThat(gmailSmtpEmailProvider.getProviderName()).isEqualTo("GMAIL_SMTP");
    }
}
