package com.scentiva.modules.observability.service;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.observability.dto.AuditLogResponse;
import com.scentiva.modules.observability.dto.SystemMetricsResponse;
import com.scentiva.modules.observability.model.AuditLog;
import com.scentiva.modules.observability.repository.AuditLogRepository;
import com.scentiva.modules.observability.service.impl.AuditLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    private AuditLog sampleLog;

    @BeforeEach
    void setUp() {
        sampleLog = AuditLog.builder()
                .id(1L)
                .userEmail("admin@scentiva.luxury")
                .action("CREATE_PRODUCT")
                .entityName("Product")
                .entityId("42")
                .ipAddress("192.168.1.100")
                .details("Created new luxury perfume flacon")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("logAction should persist audit log entry")
    void shouldLogAction() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(sampleLog);

        auditLogService.logAction("admin@scentiva.luxury", "CREATE_PRODUCT", "Product", "42", "192.168.1.100", "Created new luxury perfume flacon");

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("getAuditLogs with action filter should query by action")
    void shouldGetAuditLogsByAction() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> page = new PageImpl<>(List.of(sampleLog), pageable, 1);
        when(auditLogRepository.findByActionOrderByCreatedAtDesc("CREATE_PRODUCT", pageable)).thenReturn(page);

        ApiPaginatedResponse<AuditLogResponse> response = auditLogService.getAuditLogs("CREATE_PRODUCT", null, pageable);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().getFirst().getAction()).isEqualTo("CREATE_PRODUCT");
    }

    @Test
    @DisplayName("getAuditLogs with user email filter should query by user email")
    void shouldGetAuditLogsByUserEmail() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> page = new PageImpl<>(List.of(sampleLog), pageable, 1);
        when(auditLogRepository.findByUserEmailOrderByCreatedAtDesc("admin@scentiva.luxury", pageable)).thenReturn(page);

        ApiPaginatedResponse<AuditLogResponse> response = auditLogService.getAuditLogs(null, "admin@scentiva.luxury", pageable);

        assertThat(response).isNotNull();
        assertThat(response.getItems().getFirst().getUserEmail()).isEqualTo("admin@scentiva.luxury");
    }

    @Test
    @DisplayName("getAuditLogs with no filters should return all logs sorted by date desc")
    void shouldGetAuditLogsAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> page = new PageImpl<>(List.of(sampleLog), pageable, 1);
        when(auditLogRepository.findAllByOrderByCreatedAtDesc(pageable)).thenReturn(page);

        ApiPaginatedResponse<AuditLogResponse> response = auditLogService.getAuditLogs(null, null, pageable);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("getSystemMetrics should return JVM and runtime telemetry")
    void shouldGetSystemMetrics() {
        SystemMetricsResponse metrics = auditLogService.getSystemMetrics();

        assertThat(metrics).isNotNull();
        assertThat(metrics.getJvmVersion()).isNotEmpty();
        assertThat(metrics.getTotalMemoryMb()).isPositive();
        assertThat(metrics.getAvailableProcessors()).isPositive();
        assertThat(metrics.getStatus()).isEqualTo("HEALTHY_OPTIMAL");
    }
}
