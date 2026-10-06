package com.scentiva.modules.order.repository;

import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumberAndIsDeletedFalse(String orderNumber);

    Optional<Order> findByIdempotencyKey(String idempotencyKey);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items i WHERE o.id = :id AND o.isDeleted = false")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items i WHERE o.orderNumber = :orderNumber AND o.isDeleted = false")
    Optional<Order> findByOrderNumberWithItems(@Param("orderNumber") String orderNumber);

    Page<Order> findByCustomerIdAndIsDeletedFalse(Long customerId, Pageable pageable);

    Page<Order> findByStatusAndIsDeletedFalse(OrderStatus status, Pageable pageable);

    Page<Order> findByIsDeletedFalse(Pageable pageable);

    List<Order> findByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(Long customerId);

    Long countByIsDeletedFalse();

    Long countByStatusAndIsDeletedFalse(OrderStatus status);

    Long countByCustomerIdAndIsDeletedFalse(Long customerId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status NOT IN (com.scentiva.modules.order.model.OrderStatus.CANCELLED, com.scentiva.modules.order.model.OrderStatus.REFUNDED) AND o.isDeleted = false")
    java.math.BigDecimal sumTotalRevenue();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.customer.id = :customerId AND o.status NOT IN (com.scentiva.modules.order.model.OrderStatus.CANCELLED, com.scentiva.modules.order.model.OrderStatus.REFUNDED) AND o.isDeleted = false")
    java.math.BigDecimal sumCustomerSpend(@Param("customerId") Long customerId);
}
