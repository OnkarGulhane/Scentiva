package com.scentiva.modules.order.repository;

import com.scentiva.modules.order.model.OrderSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderSnapshotRepository extends JpaRepository<OrderSnapshot, Long> {

    Optional<OrderSnapshot> findByOrderId(Long orderId);
}
