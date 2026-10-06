package com.scentiva.modules.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Optional;

@Component
@Slf4j
public class PythonAiServiceClient {

    private final RestTemplate restTemplate;
    private final String aiServiceUrl;
    private final boolean aiEnabled;
    private final ObjectMapper objectMapper;

    public PythonAiServiceClient(
            RestTemplateBuilder builder,
            @Value("${scentiva.ai.service-url:http://localhost:8000}") String aiServiceUrl,
            @Value("${scentiva.ai.enabled:true}") boolean aiEnabled,
            @Value("${scentiva.ai.timeout-ms:10000}") int timeoutMs,
            ObjectMapper objectMapper) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(timeoutMs))
                .setReadTimeout(Duration.ofMillis(timeoutMs))
                .build();
        this.aiServiceUrl = aiServiceUrl.replaceAll("/$", "");
        this.aiEnabled = aiEnabled;
        this.objectMapper = objectMapper;
    }

    public boolean isAvailable() {
        if (!aiEnabled) return false;
        try {
            ResponseEntity<String> res = restTemplate.getForEntity(aiServiceUrl + "/health", String.class);
            return res.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.debug("Python AI Service health check failed: {}", e.getMessage());
            return false;
        }
    }

    public <T> Optional<T> post(String endpoint, Object requestBody, Class<T> responseType) {
        if (!aiEnabled) {
            return Optional.empty();
        }
        String url = aiServiceUrl + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<T> response = restTemplate.postForEntity(url, entity, responseType);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
        } catch (RestClientException e) {
            log.warn("Python AI Service error on {}: {}", endpoint, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error communicating with Python AI Service on {}: {}", endpoint, e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<JsonNode> postForJson(String endpoint, Object requestBody) {
        return post(endpoint, requestBody, JsonNode.class);
    }

    public <T> Optional<T> get(String endpoint, Class<T> responseType) {
        if (!aiEnabled) {
            return Optional.empty();
        }
        String url = aiServiceUrl + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);
        try {
            ResponseEntity<T> response = restTemplate.getForEntity(url, responseType);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
        } catch (RestClientException e) {
            log.warn("Python AI Service error on {}: {}", endpoint, e.getMessage());
        }
        return Optional.empty();
    }
}
