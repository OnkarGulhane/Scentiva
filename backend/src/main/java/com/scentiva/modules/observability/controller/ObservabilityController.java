package com.scentiva.modules.observability.controller;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.observability.dto.AuditLogResponse;
import com.scentiva.modules.observability.dto.SystemMetricsResponse;
import com.scentiva.modules.observability.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/observability")
@RequiredArgsConstructor
@Tag(name = "Observability & Audit", description = "System telemetry, JVM metrics, MDC correlation tracking, and administrative audit trail")
@SecurityRequirement(name = "bearerAuth")
public class ObservabilityController {

    private final AuditLogService auditLogService;

    @GetMapping("/metrics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get system runtime and memory telemetry", description = "Retrieves JVM memory, processor, thread, and uptime metrics")
    public ResponseEntity<ApiResponse<SystemMetricsResponse>> getMetrics() {
        SystemMetricsResponse metrics = auditLogService.getSystemMetrics();
        return ResponseEntity.ok(ApiResponse.ok(metrics, "System metrics retrieved successfully"));
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Query administrative audit logs", description = "Retrieves paginated audit log entries filtered by action or actor email")
    public ResponseEntity<ApiPaginatedResponse<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String userEmail,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        ApiPaginatedResponse<AuditLogResponse> logs = auditLogService.getAuditLogs(action, userEmail, pageable);
        return ResponseEntity.ok(logs);
    }
}
