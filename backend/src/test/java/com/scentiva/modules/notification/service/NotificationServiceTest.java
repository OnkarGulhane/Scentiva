package com.scentiva.modules.notification.service;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.notification.dto.NotificationResponse;
import com.scentiva.modules.notification.dto.NotificationSendRequest;
import com.scentiva.modules.notification.event.OrderNotificationEvent;
import com.scentiva.modules.notification.model.Notification;
import com.scentiva.modules.notification.model.NotificationChannel;
import com.scentiva.modules.notification.model.NotificationStatus;
import com.scentiva.modules.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private static final String NOTIF_USER = "notif.service@scentiva.luxury";
    private Customer customer;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();

        User user = userRepository.findByEmailAndIsDeletedFalse(NOTIF_USER)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(NOTIF_USER)
                        .passwordHash("hashed")
                        .role(Role.ROLE_CUSTOMER)
                        .build()));

        customer = customerRepository.findByUserEmail(NOTIF_USER)
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("Audrey")
                        .lastName("Hepburn")
                        .build()));
    }

    @Test
    @DisplayName("Should send manual notification to customer")
    void shouldSendManualNotification() {
        NotificationSendRequest request = NotificationSendRequest.builder()
                .customerId(customer.getId())
                .channel(NotificationChannel.EMAIL)
                .subject("VIP Invitation: Exclusive Perfumery Salon")
                .messageBody("Join our master perfumers this Saturday for private niche olfactory tastings.")
                .build();

        NotificationResponse response = notificationService.sendNotification(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo(customer.getId());
        assertThat(response.getSubject()).isEqualTo("VIP Invitation: Exclusive Perfumery Salon");
        assertThat(response.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(response.isRead()).isFalse();
    }

    @Test
    @DisplayName("Should handle order lifecycle events and generate notifications")
    void shouldHandleOrderNotificationEvents() {
        OrderNotificationEvent event = OrderNotificationEvent.builder()
                .customerId(customer.getId())
                .orderNumber("ORD-NOTIF-001")
                .eventType("CONFIRMED")
                .totalAmount(new BigDecimal("18500.00"))
                .build();

        notificationService.handleOrderNotification(event);

        List<Notification> notifications = notificationRepository.findByCustomerIdAndIsReadFalse(customer.getId());
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getSubject()).contains("Order Confirmed: ORD-NOTIF-001");
        assertThat(notifications.get(0).getMessageBody()).contains("₹18500.00");
    }

    @Test
    @DisplayName("Should retrieve unread count and mark notifications as read")
    void shouldRetrieveUnreadCountAndMarkAsRead() {
        NotificationSendRequest req1 = NotificationSendRequest.builder()
                .customerId(customer.getId())
                .subject("Message 1")
                .messageBody("Content 1")
                .build();
        NotificationSendRequest req2 = NotificationSendRequest.builder()
                .customerId(customer.getId())
                .subject("Message 2")
                .messageBody("Content 2")
                .build();

        NotificationResponse n1 = notificationService.sendNotification(req1);
        notificationService.sendNotification(req2);

        assertThat(notificationService.getUnreadCount(NOTIF_USER)).isEqualTo(2);

        // Mark first as read
        notificationService.markAsRead(NOTIF_USER, n1.getId());
        assertThat(notificationService.getUnreadCount(NOTIF_USER)).isEqualTo(1);

        // Mark all as read
        notificationService.markAllAsRead(NOTIF_USER);
        assertThat(notificationService.getUnreadCount(NOTIF_USER)).isEqualTo(0);
    }

    @Test
    @DisplayName("Should retrieve paginated customer notifications")
    void shouldGetCustomerNotifications() {
        NotificationSendRequest req = NotificationSendRequest.builder()
                .customerId(customer.getId())
                .subject("Welcome to Scentiva Club")
                .messageBody("Your olfactory journey begins.")
                .build();
        notificationService.sendNotification(req);

        ApiPaginatedResponse<NotificationResponse> list = notificationService.getCustomerNotifications(NOTIF_USER, PageRequest.of(0, 10));
        assertThat(list.getItems()).isNotEmpty();
        assertThat(list.getItems().get(0).getSubject()).isEqualTo("Welcome to Scentiva Club");
    }

    @Test
    @DisplayName("Should prevent marking notification of another customer as read")
    void shouldPreventUnauthorizedMarkAsRead() {
        User otherUser = userRepository.save(User.builder()
                .email("other.notif@scentiva.luxury")
                .passwordHash("hashed")
                .role(Role.ROLE_CUSTOMER)
                .build());

        Customer otherCustomer = customerRepository.save(Customer.builder()
                .user(otherUser)
                .firstName("Other")
                .lastName("Customer")
                .build());

        Notification notification = notificationRepository.save(Notification.builder()
                .customer(otherCustomer)
                .channel(NotificationChannel.EMAIL)
                .subject("Private Alert")
                .messageBody("Secret")
                .status(NotificationStatus.SENT)
                .isRead(false)
                .build());

        assertThatThrownBy(() -> notificationService.markAsRead(NOTIF_USER, notification.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unauthorized");
    }
}
