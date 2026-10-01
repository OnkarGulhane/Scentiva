package com.scentiva.modules.cart.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.cart.dto.AddToCartRequest;
import com.scentiva.modules.cart.dto.CartResponse;
import com.scentiva.modules.cart.dto.MergeCartRequest;
import com.scentiva.modules.cart.dto.UpdateCartItemRequest;
import com.scentiva.modules.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Shopping Bag & Cart Engine", description = "Authoritative Cart Pricing, Stock Validations & Merging")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get shopping bag", description = "Retrieves current cart for logged in customer or guest session.")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionIdHeader,
            @RequestParam(required = false) String sessionId) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        String resolvedSessionId = sessionId != null ? sessionId : sessionIdHeader;
        CartResponse cart = cartService.getCart(email, resolvedSessionId);
        return ResponseEntity.ok(ApiResponse.ok(cart, "Cart retrieved successfully"));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to shopping bag", description = "Validates stock and adds product variant.")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionIdHeader,
            @RequestParam(required = false) String sessionId,
            @Valid @RequestBody AddToCartRequest request) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        String resolvedSessionId = sessionId != null ? sessionId : sessionIdHeader;
        CartResponse cart = cartService.addToCart(email, resolvedSessionId, request);
        return ResponseEntity.ok(ApiResponse.ok(cart, "Item added to cart"));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update item quantity", description = "Updates item count with stock verification or removes if 0.")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionIdHeader,
            @RequestParam(required = false) String sessionId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        String resolvedSessionId = sessionId != null ? sessionId : sessionIdHeader;
        CartResponse cart = cartService.updateItemQuantity(email, resolvedSessionId, itemId, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.ok(cart, "Cart item updated"));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from shopping bag", description = "Deletes an item line from the cart.")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionIdHeader,
            @RequestParam(required = false) String sessionId,
            @PathVariable Long itemId) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        String resolvedSessionId = sessionId != null ? sessionId : sessionIdHeader;
        CartResponse cart = cartService.removeItem(email, resolvedSessionId, itemId);
        return ResponseEntity.ok(ApiResponse.ok(cart, "Cart item removed"));
    }

    @DeleteMapping
    @Operation(summary = "Clear shopping bag", description = "Removes all items from current cart.")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionIdHeader,
            @RequestParam(required = false) String sessionId) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        String resolvedSessionId = sessionId != null ? sessionId : sessionIdHeader;
        CartResponse cart = cartService.clearCart(email, resolvedSessionId);
        return ResponseEntity.ok(ApiResponse.ok(cart, "Cart cleared successfully"));
    }

    @PostMapping("/merge")
    @Operation(summary = "Merge guest cart into customer account", description = "Transfers guest items into customer bag upon login.")
    public ResponseEntity<ApiResponse<CartResponse>> mergeGuestCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MergeCartRequest request) {
        if (userDetails == null) {
            throw new IllegalArgumentException("Authentication required to merge cart into customer profile");
        }
        CartResponse cart = cartService.mergeGuestCart(userDetails.getUsername(), request.getGuestSessionId());
        return ResponseEntity.ok(ApiResponse.ok(cart, "Guest cart merged successfully"));
    }
}
