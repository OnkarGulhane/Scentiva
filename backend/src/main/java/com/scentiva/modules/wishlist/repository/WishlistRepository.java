package com.scentiva.modules.wishlist.repository;

import com.scentiva.modules.wishlist.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.items i LEFT JOIN FETCH i.variant v LEFT JOIN FETCH v.product p WHERE w.customer.id = :customerId AND w.isDeleted = false")
    Optional<Wishlist> findByCustomerIdWithItems(@Param("customerId") Long customerId);

    Optional<Wishlist> findByCustomerIdAndIsDeletedFalse(Long customerId);
}
