package com.scentiva.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Cross-Origin Resource Sharing (CORS) Configuration.
 * Connects the Next.js frontend (Vercel, Localhost, Custom Domains) securely with the Spring Boot backend on Render.
 */
@Configuration
public class WebCorsConfig {

    @Value("${scentiva.cors.allowed-origins:http://localhost:3000,http://127.0.0.1:3000}")
    private List<String> allowedOrigins;

    @Value("${FRONTEND_URL:http://localhost:3000}")
    private String frontendUrl;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        // Add wildcard origin patterns for Vercel deployment previews and local development
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:[*]",
                "http://127.0.0.1:[*]",
                "https://*.vercel.app",
                "https://*.onrender.com",
                "https://scentiva.vercel.app",
                "https://*.scentiva.com",
                "https://scentiva.com"
        ));

        // Add any explicit environment origins
        if (allowedOrigins != null) {
            for (String origin : allowedOrigins) {
                if (origin != null && !origin.isBlank()) {
                    config.addAllowedOriginPattern(origin.trim());
                }
            }
        }
        if (frontendUrl != null && !frontendUrl.isBlank()) {
            config.addAllowedOriginPattern(frontendUrl.trim());
        }

        config.setAllowedHeaders(List.of(
                "Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With",
                "Idempotency-Key", "X-Session-ID", "X-Correlation-ID",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));
        config.setExposedHeaders(List.of(
                "Authorization", "Set-Cookie", "Idempotency-Key", "X-Total-Count", "X-Correlation-ID"
        ));
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
        ));
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
