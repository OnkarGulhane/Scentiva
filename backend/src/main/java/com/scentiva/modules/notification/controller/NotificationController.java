package com.scentiva.modules.notification.controller;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.notification.dto.NotificationResponse;
import com.scentiva.modules.notification.dto.NotificationSendRequest;
import com.scentiva.modules.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Customer Notifications & Communications", description = "Transactional alerts, dispatch updates, promotional news and message centre")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get customer notifications", description = "Retrieves paginated notifications for the logged in customer.")
    public ResponseEntity<ApiPaginatedResponse<NotificationResponse>> getCustomerNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        String email = userDetails.getUsername();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));
        ApiPaginatedResponse<NotificationResponse> response = notificationService.getCustomerNotifications(email, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notifications count", description = "Returns total number of unread alerts for customer badge.")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        long count = notificationService.getUnreadCount(email);
        return ResponseEntity.ok(ApiResponse.ok(count, "Unread count retrieved"));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Marks an alert as read by ID.")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        notificationService.markAsRead(email, id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Notification marked as read"));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read", description = "Marks all unread alerts as read for current user.")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        notificationService.markAllAsRead(email);
        return ResponseEntity.ok(ApiResponse.ok(null, "All notifications marked as read"));
    }

    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'CUSTOMER_SUPPORT')")
    @Operation(summary = "Send notification (Admin)", description = "Dispatches a manual notification to a customer.")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendNotification(@Valid @RequestBody NotificationSendRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Notification dispatched successfully"));
    }
}
