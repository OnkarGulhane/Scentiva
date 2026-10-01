package com.scentiva.modules.wishlist.repository;

import com.scentiva.modules.wishlist.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByWishlistId(Long wishlistId);

    Optional<WishlistItem> findByWishlistIdAndVariantId(Long wishlistId, Long variantId);

    void deleteByWishlistIdAndVariantId(Long wishlistId, Long variantId);
}
