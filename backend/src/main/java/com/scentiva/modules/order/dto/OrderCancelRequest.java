package com.scentiva.modules.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelRequest {

    @NotBlank(message = "Cancellation reason is required")
    private String reason;
}
