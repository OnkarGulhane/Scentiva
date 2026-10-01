package com.scentiva.modules.notification.service;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.notification.dto.NotificationResponse;
import com.scentiva.modules.notification.dto.NotificationSendRequest;
import com.scentiva.modules.notification.event.OrderNotificationEvent;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    NotificationResponse sendNotification(NotificationSendRequest request);

    ApiPaginatedResponse<NotificationResponse> getCustomerNotifications(String email, Pageable pageable);

    long getUnreadCount(String email);

    void markAsRead(String email, Long notificationId);

    void markAllAsRead(String email);

    void handleOrderNotification(OrderNotificationEvent event);
}
