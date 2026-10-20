package com.scentiva.modules.notification.provider;

import com.scentiva.modules.notification.provider.dto.EmailPayload;
import com.scentiva.modules.notification.provider.impl.BrevoEmailProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrevoEmailProviderTest {

    @Mock
    private RestTemplate restTemplate;

    @Test
    @DisplayName("Should report not available when api-key is placeholder or empty")
    void testAvailabilityCheck() {
        BrevoEmailProvider provider = new BrevoEmailProvider(restTemplate);
        ReflectionTestUtils.setField(provider, "apiKey", "placeholder_brevo_api_key");
        assertThat(provider.isAvailable()).isFalse();

        ReflectionTestUtils.setField(provider, "apiKey", "");
        assertThat(provider.isAvailable()).isFalse();

        ReflectionTestUtils.setField(provider, "apiKey", "xkeysib-real-active-api-key");
        assertThat(provider.isAvailable()).isTrue();
        assertThat(provider.getProviderName()).isEqualTo("BREVO");
    }

    @Test
    @DisplayName("Should return false without calling HTTP when unconfigured")
    void testSendEmailWhenUnconfigured() {
        BrevoEmailProvider provider = new BrevoEmailProvider(restTemplate);
        ReflectionTestUtils.setField(provider, "apiKey", "placeholder_brevo_api_key");

        EmailPayload payload = EmailPayload.builder()
                .to("client@example.com")
                .subject("Test")
                .htmlContent("<p>Test</p>")
                .build();

        boolean result = provider.sendEmail(payload);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should successfully send email when Brevo API responds with 201 Created")
    void testSendEmailSuccess() {
        BrevoEmailProvider provider = new BrevoEmailProvider(restTemplate);
        ReflectionTestUtils.setField(provider, "apiKey", "xkeysib-valid-key-9999");
        ReflectionTestUtils.setField(provider, "apiUrl", "https://api.brevo.com/v3/smtp/email");
        ReflectionTestUtils.setField(provider, "defaultFromEmail", "SCENTIVA <orders@scentiva.com>");
        ReflectionTestUtils.setField(provider, "defaultReplyTo", "concierge@scentiva.com");

        when(restTemplate.postForEntity(eq("https://api.brevo.com/v3/smtp/email"), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"messageId\":\"<123456@smtp-relay.brevo.com>\"}", HttpStatus.CREATED));

        EmailPayload payload = EmailPayload.builder()
                .to("client@scentiva.luxury")
                .subject("Your Haute Parfumerie Acquisition")
                .htmlContent("<h1>Welcome to Scentiva</h1>")
                .cc(List.of("vip@scentiva.luxury"))
                .build();

        boolean result = provider.sendEmail(payload);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should handle API errors gracefully and return false without throwing uncaught exceptions")
    void testSendEmailFailureHandling() {
        BrevoEmailProvider provider = new BrevoEmailProvider(restTemplate);
        ReflectionTestUtils.setField(provider, "apiKey", "xkeysib-valid-key-9999");
        ReflectionTestUtils.setField(provider, "apiUrl", "https://api.brevo.com/v3/smtp/email");
        ReflectionTestUtils.setField(provider, "defaultFromEmail", "SCENTIVA <orders@scentiva.com>");

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Brevo API 503 Service Unavailable"));

        EmailPayload payload = EmailPayload.builder()
                .to("client@scentiva.luxury")
                .subject("Test")
                .htmlContent("<p>Test</p>")
                .build();

        boolean result = provider.sendEmail(payload);
        assertThat(result).isFalse();
    }
}
