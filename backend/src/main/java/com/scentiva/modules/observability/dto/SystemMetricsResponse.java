package com.scentiva.modules.observability.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemMetricsResponse {
    private String jvmVersion;
    private long uptimeSeconds;
    private long totalMemoryMb;
    private long freeMemoryMb;
    private long usedMemoryMb;
    private int availableProcessors;
    private int activeThreadCount;
    private String status;
}
