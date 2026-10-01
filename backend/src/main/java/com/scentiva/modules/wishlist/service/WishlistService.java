package com.scentiva.modules.wishlist.service;

import com.scentiva.modules.wishlist.dto.WishlistResponse;

public interface WishlistService {

    WishlistResponse getWishlist(String email);

    WishlistResponse addToWishlist(String email, Long variantId);

    WishlistResponse removeFromWishlist(String email, Long variantId);

    WishlistResponse clearWishlist(String email);
}
