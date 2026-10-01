package com.scentiva.modules.returns.dto;

import com.scentiva.modules.returns.model.ReturnStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private ReturnStatus status;

    private String pickupTrackingNumber;
    private String adminNotes;
}
