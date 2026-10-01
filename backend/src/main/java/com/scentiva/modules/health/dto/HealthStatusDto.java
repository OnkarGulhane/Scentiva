package com.scentiva.modules.health.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health Status DTO for system diagnostics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "System Health Status DTO")
public class HealthStatusDto {

    @Schema(description = "Overall status", example = "UP")
    private String status;

    @Schema(description = "Application name", example = "SCENTIVA — Haute Parfumerie")
    private String application;

    @Schema(description = "Application version", example = "1.0.0-RELEASE")
    private String version;

    @Schema(description = "Active environment profile", example = "local")
    private String environment;

    @Schema(description = "System timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "Detailed subsystem components status")
    private Map<String, String> components;
}
