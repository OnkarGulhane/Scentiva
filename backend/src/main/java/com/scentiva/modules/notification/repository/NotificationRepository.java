package com.scentiva.modules.notification.repository;

import com.scentiva.modules.notification.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByCustomerIdOrderBySentAtDesc(Long customerId, Pageable pageable);

    List<Notification> findByCustomerIdAndIsReadFalse(Long customerId);

    long countByCustomerIdAndIsReadFalse(Long customerId);
}
