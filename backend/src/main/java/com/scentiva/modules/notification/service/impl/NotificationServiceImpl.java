package com.scentiva.modules.notification.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.notification.dto.NotificationResponse;
import com.scentiva.modules.notification.dto.NotificationSendRequest;
import com.scentiva.modules.notification.event.OrderNotificationEvent;
import com.scentiva.modules.notification.model.Notification;
import com.scentiva.modules.notification.model.NotificationChannel;
import com.scentiva.modules.notification.model.NotificationStatus;
import com.scentiva.modules.notification.repository.NotificationRepository;
import com.scentiva.modules.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;
    private final com.scentiva.modules.notification.service.EmailService emailService;

    @Override
    @Transactional
    public NotificationResponse sendNotification(NotificationSendRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        Notification notification = Notification.builder()
                .customer(customer)
                .channel(request.getChannel() != null ? request.getChannel() : NotificationChannel.EMAIL)
                .subject(request.getSubject())
                .messageBody(request.getMessageBody())
                .status(NotificationStatus.SENT)
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        notification = notificationRepository.save(notification);
        log.info("Dispatched notification id={} to customer={}, subject='{}'",
                notification.getId(), customer.getUser().getEmail(), notification.getSubject());

        if (notification.getChannel() == NotificationChannel.EMAIL && customer.getUser() != null && customer.getUser().getEmail() != null) {
            emailService.sendHtmlEmail(customer.getUser().getEmail(), notification.getSubject(), notification.getMessageBody());
        }

        return mapToNotificationResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiPaginatedResponse<NotificationResponse> getCustomerNotifications(String email, Pageable pageable) {
        Customer customer = getCustomerByEmail(email);
        Page<Notification> page = notificationRepository.findByCustomerIdOrderBySentAtDesc(customer.getId(), pageable);

        List<NotificationResponse> items = page.getContent().stream()
                .map(this::mapToNotificationResponse)
                .toList();

        return ApiPaginatedResponse.of(items, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {
        Customer customer = getCustomerByEmail(email);
        return notificationRepository.countByCustomerIdAndIsReadFalse(customer.getId());
    }

    @Override
    @Transactional
    public void markAsRead(String email, Long notificationId) {
        Customer customer = getCustomerByEmail(email);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Unauthorized notification access");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(String email) {
        Customer customer = getCustomerByEmail(email);
        List<Notification> unread = notificationRepository.findByCustomerIdAndIsReadFalse(customer.getId());
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    @Override
    @EventListener
    @Transactional
    public void handleOrderNotification(OrderNotificationEvent event) {
        if (event.getCustomerId() == null) return;

        Customer customer = customerRepository.findById(event.getCustomerId()).orElse(null);
        if (customer == null) return;

        String subject;
        String body;

        switch (event.getEventType()) {
            case "CONFIRMED" -> {
                subject = "Order Confirmed: " + event.getOrderNumber();
                body = "Dear " + customer.getFirstName() + ", your Scentiva order " + event.getOrderNumber() +
                        " has been confirmed. Total: ₹" + event.getTotalAmount() + ". Our perfumers are preparing your shipment.";
            }
            case "SHIPPED" -> {
                subject = "Your Scentiva Order is on its way: " + event.getOrderNumber();
                body = "Your package has been dispatched via " + (event.getCarrierName() != null ? event.getCarrierName() : "Luxury Courier") +
                        ". Tracking Number: " + event.getTrackingNumber();
            }
            case "DELIVERED" -> {
                subject = "Order Delivered: " + event.getOrderNumber();
                body = "Your Scentiva fragrance has been delivered. We hope you enjoy your exquisite olfactory journey.";
            }
            case "CANCELLED" -> {
                subject = "Order Cancelled: " + event.getOrderNumber();
                body = "Your order " + event.getOrderNumber() + " has been cancelled. Any applicable refund has been initiated.";
            }
            default -> {
                subject = "Update on your order: " + event.getOrderNumber();
                body = "Your order " + event.getOrderNumber() + " status is now " + event.getEventType();
            }
        }

        Notification notification = Notification.builder()
                .customer(customer)
                .channel(NotificationChannel.EMAIL)
                .subject(subject)
                .messageBody(body)
                .status(NotificationStatus.SENT)
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("Saved automated order notification for order={}, event={}", event.getOrderNumber(), event.getEventType());

        if (customer.getUser() != null && customer.getUser().getEmail() != null) {
            String recipientEmail = customer.getUser().getEmail();
            String customerName = customer.getFirstName();

            switch (event.getEventType()) {
                case "CONFIRMED" -> emailService.sendOrderConfirmationEmail(
                        recipientEmail, customerName, event.getOrderNumber(), event.getTotalAmount(), null, null, null);
                case "SHIPPED" -> emailService.sendOrderShippedEmail(
                        recipientEmail, customerName, event.getOrderNumber(), event.getCarrierName(), event.getTrackingNumber(), null);
                case "DELIVERED" -> emailService.sendOrderDeliveredEmail(
                        recipientEmail, customerName, event.getOrderNumber(), null);
                case "CANCELLED" -> emailService.sendOrderCancelledEmail(
                        recipientEmail, customerName, event.getOrderNumber(), event.getTotalAmount());
                default -> emailService.sendHtmlEmail(recipientEmail, subject, body);
            }
        }
    }

    private Customer getCustomerByEmail(String email) {
        return customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .customerId(notification.getCustomer().getId())
                .channel(notification.getChannel())
                .subject(notification.getSubject())
                .messageBody(notification.getMessageBody())
                .status(notification.getStatus())
                .sentAt(notification.getSentAt())
                .isRead(notification.isRead())
                .build();
    }
}
