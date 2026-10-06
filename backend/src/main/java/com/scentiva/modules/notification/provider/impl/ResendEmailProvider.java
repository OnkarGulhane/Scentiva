package com.scentiva.modules.notification.provider.impl;

import com.scentiva.modules.notification.provider.EmailProvider;
import com.scentiva.modules.notification.provider.dto.EmailPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Resend Email Provider SPI implementation utilizing the modern Resend REST API (https://api.resend.com/emails).
 * Delivers sub-500ms transactional email dispatch over HTTPS port 443.
 */
@Slf4j
@Component("resendEmailProvider")
public class ResendEmailProvider implements EmailProvider {

    private final RestTemplate restTemplate;

    @Value("${scentiva.mail.resend.api-key:}")
    private String apiKey;

    @Value("${scentiva.mail.resend.api-url:https://api.resend.com/emails}")
    private String apiUrl;

    @Value("${scentiva.mail.from-email:SCENTIVA <orders@scentiva.com>}")
    private String defaultFromEmail;

    @Value("${scentiva.mail.reply-to:concierge@scentiva.com}")
    private String defaultReplyTo;

    public ResendEmailProvider() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(4000);
        factory.setReadTimeout(6000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public boolean sendEmail(EmailPayload payload) {
        if (!isAvailable()) {
            log.warn("[Resend] API key is not configured or is a placeholder. Simulating Resend dispatch to={}", payload.getTo());
            return false;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey.trim());

            String sender = (payload.getFromEmail() != null && !payload.getFromEmail().isBlank())
                    ? (payload.getFromName() != null ? payload.getFromName() + " <" + payload.getFromEmail() + ">" : payload.getFromEmail())
                    : defaultFromEmail;

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("from", sender);
            requestBody.put("to", Collections.singletonList(payload.getTo()));
            requestBody.put("subject", payload.getSubject());

            if (payload.getHtmlContent() != null && !payload.getHtmlContent().isBlank()) {
                requestBody.put("html", payload.getHtmlContent());
            } else if (payload.getTextContent() != null) {
                requestBody.put("text", payload.getTextContent());
            } else {
                requestBody.put("text", "No content provided");
            }

            if (payload.getReplyTo() != null && !payload.getReplyTo().isBlank()) {
                requestBody.put("reply_to", payload.getReplyTo());
            } else if (defaultReplyTo != null && !defaultReplyTo.isBlank()) {
                requestBody.put("reply_to", defaultReplyTo);
            }

            if (payload.getCc() != null && !payload.getCc().isEmpty()) {
                requestBody.put("cc", payload.getCc());
            }
            if (payload.getBcc() != null && !payload.getBcc().isEmpty()) {
                requestBody.put("bcc", payload.getBcc());
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            log.info("[Resend] Sending email to={}, subject='{}'", payload.getTo(), payload.getSubject());

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("[Resend] Successfully delivered email to={} (HTTP {})", payload.getTo(), response.getStatusCode());
                return true;
            } else {
                log.warn("[Resend] Delivery returned non-2xx status: {} body={}", response.getStatusCode(), response.getBody());
                return false;
            }
        } catch (Exception ex) {
            log.error("[Resend] Failed to send email to {}: {}", payload.getTo(), ex.getMessage());
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return "RESEND";
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.contains("placeholder");
    }
}
