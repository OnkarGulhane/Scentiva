package com.scentiva.modules.notification.provider.impl;

import com.scentiva.modules.notification.provider.EmailProvider;
import com.scentiva.modules.notification.provider.dto.EmailPayload;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Gmail SMTP Email Provider SPI implementation utilizing JavaMailSender over SMTP/TLS port 587.
 */
@Slf4j
@Component("gmailSmtpEmailProvider")
public class GmailSmtpEmailProvider implements EmailProvider {

    @Value("${scentiva.mail.gmail.host:smtp.gmail.com}")
    private String host;

    @Value("${scentiva.mail.gmail.port:587}")
    private int port;

    @Value("${scentiva.mail.gmail.username:}")
    private String username;

    @Value("${scentiva.mail.gmail.password:}")
    private String password;

    @Value("${scentiva.mail.from-email:SCENTIVA <orders.scentiva@gmail.com>}")
    private String defaultFromEmail;

    @Value("${scentiva.mail.reply-to:concierge@scentiva.com}")
    private String defaultReplyTo;

    private JavaMailSender createSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        return mailSender;
    }

    @Override
    public boolean sendEmail(EmailPayload payload) {
        if (!isAvailable()) {
            log.warn("[Gmail SMTP] Credentials not configured or are placeholder. Simulating dispatch to={}", payload.getTo());
            return false;
        }

        try {
            JavaMailSender sender = createSender();
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String senderAddress = (payload.getFromEmail() != null && !payload.getFromEmail().isBlank())
                    ? payload.getFromEmail()
                    : (username != null && !username.isBlank() ? username : "orders.scentiva@gmail.com");

            String senderName = (payload.getFromName() != null && !payload.getFromName().isBlank())
                    ? payload.getFromName()
                    : "SCENTIVA Haute Parfumerie";

            helper.setFrom(senderAddress, senderName);
            helper.setTo(payload.getTo());
            helper.setSubject(payload.getSubject());

            if (payload.getHtmlContent() != null && !payload.getHtmlContent().isBlank()) {
                helper.setText(payload.getHtmlContent(), true);
            } else if (payload.getTextContent() != null) {
                helper.setText(payload.getTextContent(), false);
            }

            if (payload.getReplyTo() != null && !payload.getReplyTo().isBlank()) {
                helper.setReplyTo(payload.getReplyTo());
            } else if (defaultReplyTo != null && !defaultReplyTo.isBlank()) {
                helper.setReplyTo(defaultReplyTo);
            }

            if (payload.getCc() != null && !payload.getCc().isEmpty()) {
                helper.setCc(payload.getCc().toArray(new String[0]));
            }
            if (payload.getBcc() != null && !payload.getBcc().isEmpty()) {
                helper.setBcc(payload.getBcc().toArray(new String[0]));
            }

            log.info("[Gmail SMTP] Sending email to={}, subject='{}'", payload.getTo(), payload.getSubject());
            sender.send(mimeMessage);
            log.info("[Gmail SMTP] Successfully delivered email to={}", payload.getTo());
            return true;
        } catch (Exception ex) {
            log.error("[Gmail SMTP] Failed to send email to {}: {}", payload.getTo(), ex.getMessage());
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return "GMAIL_SMTP";
    }

    @Override
    public boolean isAvailable() {
        return username != null && !username.isBlank() && !username.contains("placeholder")
                && password != null && !password.isBlank() && !password.contains("placeholder");
    }
}
