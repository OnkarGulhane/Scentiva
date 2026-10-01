package com.scentiva.modules.wishlist.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.wishlist.dto.WishlistResponse;
import com.scentiva.modules.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Customer Wishlist", description = "Personal Fragrance Curation & Saved Fragrances")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get customer wishlist", description = "Retrieves all saved fragrance items with real-time stock and pricing.")
    public ResponseEntity<ApiResponse<WishlistResponse>> getWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        WishlistResponse wishlist = wishlistService.getWishlist(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(wishlist, "Wishlist retrieved successfully"));
    }

    @PostMapping("/{variantId}")
    @Operation(summary = "Add fragrance variant to wishlist", description = "Saves variant for later purchase.")
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long variantId) {
        WishlistResponse wishlist = wishlistService.addToWishlist(userDetails.getUsername(), variantId);
        return ResponseEntity.ok(ApiResponse.ok(wishlist, "Added to wishlist"));
    }

    @DeleteMapping("/{variantId}")
    @Operation(summary = "Remove fragrance variant from wishlist", description = "Removes variant from customer saved list.")
    public ResponseEntity<ApiResponse<WishlistResponse>> removeFromWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long variantId) {
        WishlistResponse wishlist = wishlistService.removeFromWishlist(userDetails.getUsername(), variantId);
        return ResponseEntity.ok(ApiResponse.ok(wishlist, "Removed from wishlist"));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear entire wishlist", description = "Removes all saved items from customer wishlist.")
    public ResponseEntity<ApiResponse<WishlistResponse>> clearWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        WishlistResponse wishlist = wishlistService.clearWishlist(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(wishlist, "Wishlist cleared successfully"));
    }
}
