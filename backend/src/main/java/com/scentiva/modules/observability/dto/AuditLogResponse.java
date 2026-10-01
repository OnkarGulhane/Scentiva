package com.scentiva.modules.observability.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private String userEmail;
    private String action;
    private String entityName;
    private String entityId;
    private String ipAddress;
    private String details;
    private LocalDateTime createdAt;
}
