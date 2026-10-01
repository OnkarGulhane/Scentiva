package com.scentiva.modules.notification.dto;

import com.scentiva.modules.notification.model.NotificationChannel;
import com.scentiva.modules.notification.model.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long customerId;
    private NotificationChannel channel;
    private String subject;
    private String messageBody;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private boolean isRead;
}
