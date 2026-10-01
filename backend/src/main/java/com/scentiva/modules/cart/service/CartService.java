package com.scentiva.modules.cart.service;

import com.scentiva.modules.cart.dto.AddToCartRequest;
import com.scentiva.modules.cart.dto.CartResponse;

public interface CartService {

    CartResponse getCart(String email, String sessionId);

    CartResponse addToCart(String email, String sessionId, AddToCartRequest request);

    CartResponse updateItemQuantity(String email, String sessionId, Long itemId, int quantity);

    CartResponse removeItem(String email, String sessionId, Long itemId);

    CartResponse clearCart(String email, String sessionId);

    CartResponse mergeGuestCart(String email, String guestSessionId);
}
