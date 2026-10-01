package com.scentiva.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 / Swagger Documentation Configuration for SCENTIVA.
 */
@Configuration
public class OpenApiConfig {

    @Value("${scentiva.app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    @Bean
    public OpenAPI scentivaOpenAPI() {
        final String securitySchemeName = "BearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("SCENTIVA — Haute Parfumerie & Marketplace API")
                        .description("Production-oriented REST API for SCENTIVA Multi-Brand Fragrance Platform. " +
                                "Provides catalog discovery, olfactory search, multi-location inventory, authoritative cart/checkout, " +
                                "provider abstractions (Razorpay, Shiprocket), and AI concierge capabilities.")
                        .version("1.0.0-RELEASE")
                        .contact(new Contact()
                                .name("SCENTIVA Engineering")
                                .email("engineering@scentiva.luxury")
                                .url("https://scentiva.luxury"))
                        .license(new License()
                                .name("Proprietary & Confidential")
                                .url("https://scentiva.luxury/policies/terms")))
                .servers(List.of(
                        new Server().url(appBaseUrl).description("Primary Application Server"),
                        new Server().url("http://localhost:8080").description("Local Development Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT Bearer token to access protected customer and admin endpoints.")));
    }
}
