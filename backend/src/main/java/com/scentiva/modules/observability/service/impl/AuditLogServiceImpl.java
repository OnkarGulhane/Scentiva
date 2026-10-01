package com.scentiva.modules.observability.service.impl;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.observability.dto.AuditLogResponse;
import com.scentiva.modules.observability.dto.SystemMetricsResponse;
import com.scentiva.modules.observability.model.AuditLog;
import com.scentiva.modules.observability.repository.AuditLogRepository;
import com.scentiva.modules.observability.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void logAction(String userEmail, String action, String entityName, String entityId, String ipAddress, String details) {
        AuditLog auditLog = AuditLog.builder()
                .userEmail(userEmail != null ? userEmail : "SYSTEM")
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .ipAddress(ipAddress)
                .details(details)
                .build();

        auditLogRepository.save(auditLog);
        log.info("Audit: user='{}' action='{}' entity='{}' id='{}'", userEmail, action, entityName, entityId);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiPaginatedResponse<AuditLogResponse> getAuditLogs(String action, String userEmail, Pageable pageable) {
        Page<AuditLog> page;
        if (action != null && !action.trim().isEmpty()) {
            page = auditLogRepository.findByActionOrderByCreatedAtDesc(action.trim(), pageable);
        } else if (userEmail != null && !userEmail.trim().isEmpty()) {
            page = auditLogRepository.findByUserEmailOrderByCreatedAtDesc(userEmail.trim(), pageable);
        } else {
            page = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        List<AuditLogResponse> items = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return ApiPaginatedResponse.of(items, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Override
    public SystemMetricsResponse getSystemMetrics() {
        Runtime runtime = Runtime.getRuntime();
        long totalMem = runtime.totalMemory() / (1024 * 1024);
        long freeMem = runtime.freeMemory() / (1024 * 1024);
        long usedMem = totalMem - freeMem;
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        int activeThreads = Thread.activeCount();

        return SystemMetricsResponse.builder()
                .jvmVersion(System.getProperty("java.version"))
                .uptimeSeconds(uptime)
                .totalMemoryMb(totalMem)
                .freeMemoryMb(freeMem)
                .usedMemoryMb(usedMem)
                .availableProcessors(runtime.availableProcessors())
                .activeThreadCount(activeThreads)
                .status("HEALTHY_OPTIMAL")
                .build();
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .userEmail(log.getUserEmail())
                .action(log.getAction())
                .entityName(log.getEntityName())
                .entityId(log.getEntityId())
                .ipAddress(log.getIpAddress())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
