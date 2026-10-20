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
 * Brevo (formerly Sendinblue) Email Provider SPI implementation utilizing the Brevo REST API v3 (https://api.brevo.com/v3/smtp/email).
 * Dispatches transactional luxury emails over HTTPS port 443 with sub-second latency.
 */
@Slf4j
@Component("brevoEmailProvider")
public class BrevoEmailProvider implements EmailProvider {

    private final RestTemplate restTemplate;

    @Value("${scentiva.mail.brevo.api-key:}")
    private String apiKey;

    @Value("${scentiva.mail.brevo.api-url:https://api.brevo.com/v3/smtp/email}")
    private String apiUrl;

    @Value("${scentiva.mail.from-email:SCENTIVA <orders@scentiva.com>}")
    private String defaultFromEmail;

    @Value("${scentiva.mail.reply-to:concierge@scentiva.com}")
    private String defaultReplyTo;

    public BrevoEmailProvider() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(4000);
        factory.setReadTimeout(6000);
        this.restTemplate = new RestTemplate(factory);
    }

    public BrevoEmailProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean sendEmail(EmailPayload payload) {
        if (!isAvailable()) {
            log.warn("[Brevo] API key is not configured or is a placeholder. Simulating Brevo dispatch to={}", payload.getTo());
            return false;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("api-key", apiKey.trim());

            // Parse Sender Name and Email
            String rawFrom = (payload.getFromEmail() != null && !payload.getFromEmail().isBlank())
                    ? payload.getFromEmail() : defaultFromEmail;
            String senderEmail = rawFrom;
            String senderName = payload.getFromName();

            if (rawFrom != null && rawFrom.contains("<") && rawFrom.contains(">")) {
                int start = rawFrom.indexOf("<");
                int end = rawFrom.indexOf(">");
                senderEmail = rawFrom.substring(start + 1, end).trim();
                if (senderName == null || senderName.isBlank()) {
                    senderName = rawFrom.substring(0, start).trim();
                }
            }
            if (senderName == null || senderName.isBlank()) {
                senderName = "SCENTIVA Haute Parfumerie";
            }

            Map<String, Object> requestBody = new HashMap<>();

            Map<String, String> sender = new HashMap<>();
            sender.put("name", senderName);
            sender.put("email", senderEmail);
            requestBody.put("sender", sender);

            // Recipient list
            List<Map<String, String>> toList = new ArrayList<>();
            Map<String, String> recipient = new HashMap<>();
            recipient.put("email", payload.getTo());
            toList.add(recipient);
            requestBody.put("to", toList);

            requestBody.put("subject", payload.getSubject());

            if (payload.getHtmlContent() != null && !payload.getHtmlContent().isBlank()) {
                requestBody.put("htmlContent", payload.getHtmlContent());
            } else if (payload.getTextContent() != null) {
                requestBody.put("textContent", payload.getTextContent());
            } else {
                requestBody.put("textContent", "No content provided");
            }

            // Reply-To
            String replyToEmail = (payload.getReplyTo() != null && !payload.getReplyTo().isBlank())
                    ? payload.getReplyTo() : defaultReplyTo;
            if (replyToEmail != null && !replyToEmail.isBlank()) {
                Map<String, String> replyToMap = new HashMap<>();
                replyToMap.put("email", replyToEmail);
                replyToMap.put("name", "SCENTIVA Concierge");
                requestBody.put("replyTo", replyToMap);
            }

            // CC / BCC
            if (payload.getCc() != null && !payload.getCc().isEmpty()) {
                List<Map<String, String>> ccList = new ArrayList<>();
                for (String ccEmail : payload.getCc()) {
                    ccList.add(Map.of("email", ccEmail));
                }
                requestBody.put("cc", ccList);
            }
            if (payload.getBcc() != null && !payload.getBcc().isEmpty()) {
                List<Map<String, String>> bccList = new ArrayList<>();
                for (String bccEmail : payload.getBcc()) {
                    bccList.add(Map.of("email", bccEmail));
                }
                requestBody.put("bcc", bccList);
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            log.info("[Brevo] Sending email to={}, subject='{}'", payload.getTo(), payload.getSubject());

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("[Brevo] Successfully delivered email to={} (HTTP {})", payload.getTo(), response.getStatusCode());
                return true;
            } else {
                log.warn("[Brevo] Delivery returned non-2xx status: {} body={}", response.getStatusCode(), response.getBody());
                return false;
            }
        } catch (Exception ex) {
            log.error("[Brevo] Failed to send email to {}: {}", payload.getTo(), ex.getMessage());
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return "BREVO";
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.contains("placeholder");
    }
}
