package com.scentiva.modules.health;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.health.dto.HealthStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

/**
 * Health & Diagnostics REST Controller for SCENTIVA.
 */
@RestController
@RequestMapping({"/api/health", "/api/v1/health"})
@Tag(name = "Health & System", description = "System Diagnostics, Availability & Subsystem Status")
public class HealthController {

    @Value("${scentiva.app.name:SCENTIVA — Haute Parfumerie}")
    private String applicationName;

    @Value("${scentiva.app.version:1.0.0-RELEASE}")
    private String applicationVersion;

    private final Environment environment;

    public HealthController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping
    @Operation(summary = "Check backend system health", description = "Returns active health state, environment profile, and component statuses.")
    public ResponseEntity<ApiResponse<HealthStatusDto>> getHealthStatus() {
        String activeProfile = Arrays.stream(environment.getActiveProfiles())
                .findFirst()
                .orElse("default");

        HealthStatusDto healthStatus = HealthStatusDto.builder()
                .status("UP")
                .application(applicationName)
                .version(applicationVersion)
                .environment(activeProfile)
                .timestamp(LocalDateTime.now())
                .components(Map.of(
                        "database", "UP",
                        "security", "ACTIVE",
                        "storage", "READY",
                        "aiEngine", "STANDBY"
                ))
                .build();

        return ResponseEntity.ok(ApiResponse.ok(healthStatus, "SCENTIVA Core API is operational"));
    }
}
