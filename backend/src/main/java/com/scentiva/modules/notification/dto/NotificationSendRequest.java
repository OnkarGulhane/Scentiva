package com.scentiva.modules.notification.dto;

import com.scentiva.modules.notification.model.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSendRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @Builder.Default
    private NotificationChannel channel = NotificationChannel.EMAIL;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Message body is required")
    private String messageBody;
}
