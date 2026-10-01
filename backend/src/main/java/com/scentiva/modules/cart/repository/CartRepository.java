package com.scentiva.modules.cart.repository;

import com.scentiva.modules.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.variant v LEFT JOIN FETCH v.product p WHERE c.customer.id = :customerId AND c.isDeleted = false")
    Optional<Cart> findByCustomerIdWithItems(@Param("customerId") Long customerId);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.variant v LEFT JOIN FETCH v.product p WHERE c.sessionId = :sessionId AND c.customer IS NULL AND c.isDeleted = false")
    Optional<Cart> findBySessionIdWithItems(@Param("sessionId") String sessionId);

    Optional<Cart> findByCustomerIdAndIsDeletedFalse(Long customerId);

    Optional<Cart> findBySessionIdAndCustomerIdIsNullAndIsDeletedFalse(String sessionId);
}
