package com.scentiva.modules.returns.dto;

import com.scentiva.modules.returns.model.ReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnResponse {
    private Long id;
    private String returnNumber;
    private Long orderId;
    private String orderNumber;
    private Long customerId;
    private String customerName;
    private ReturnStatus status;
    private String reason;
    private BigDecimal refundAmount;
    private String pickupTrackingNumber;
    private String adminNotes;
    private List<ReturnItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
