package com.scentiva.modules.cart.repository;

import com.scentiva.modules.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartIdAndIsDeletedFalse(Long cartId);

    Optional<CartItem> findByCartIdAndVariantIdAndIsDeletedFalse(Long cartId, Long variantId);

    Optional<CartItem> findByIdAndCartIdAndIsDeletedFalse(Long id, Long cartId);
}
