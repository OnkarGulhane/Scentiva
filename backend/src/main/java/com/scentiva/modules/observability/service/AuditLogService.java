package com.scentiva.modules.observability.service;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.observability.dto.AuditLogResponse;
import com.scentiva.modules.observability.dto.SystemMetricsResponse;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {

    void logAction(String userEmail, String action, String entityName, String entityId, String ipAddress, String details);

    ApiPaginatedResponse<AuditLogResponse> getAuditLogs(String action, String userEmail, Pageable pageable);

    SystemMetricsResponse getSystemMetrics();
}
