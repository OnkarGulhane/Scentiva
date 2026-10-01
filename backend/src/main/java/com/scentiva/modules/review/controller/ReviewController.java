package com.scentiva.modules.review.controller;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.review.dto.*;
import com.scentiva.modules.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Customer Reviews & Fragrance Ratings", description = "Verified buyer fragrance reviews, olfactory longevity ratings and editorial moderation")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get approved reviews for product", description = "Retrieves paginated verified customer reviews for product detail page.")
    public ResponseEntity<ApiPaginatedResponse<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ApiPaginatedResponse<ReviewResponse> response = reviewService.getApprovedReviewsByProduct(productId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}/summary")
    @Operation(summary = "Get product rating summary", description = "Calculates average star rating and total approved review count.")
    public ResponseEntity<ApiResponse<ProductReviewSummaryResponse>> getProductSummary(@PathVariable Long productId) {
        ProductReviewSummaryResponse response = reviewService.getProductReviewSummary(productId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Review summary retrieved"));
    }

    @PostMapping
    @Operation(summary = "Submit customer review", description = "Submits a rating and comment. Automatically sets verified purchase if ordered.")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewCreateRequest request) {
        String email = userDetails.getUsername();
        ReviewResponse response = reviewService.createReview(email, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Review submitted successfully"));
    }

    @PutMapping("/{reviewId}/moderate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'CONTENT_MODERATOR', 'PRODUCT_MANAGER')")
    @Operation(summary = "Moderate review (Admin)", description = "Approves or rejects customer review submission.")
    public ResponseEntity<ApiResponse<ReviewResponse>> moderateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewModerationRequest request) {
        ReviewResponse response = reviewService.moderateReview(reviewId, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Review moderated successfully"));
    }

    @GetMapping("/admin/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'CONTENT_MODERATOR', 'PRODUCT_MANAGER')")
    @Operation(summary = "Get pending reviews (Admin)", description = "Lists reviews awaiting editorial moderation.")
    public ResponseEntity<ApiPaginatedResponse<ReviewResponse>> getPendingReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ApiPaginatedResponse<ReviewResponse> response = reviewService.getPendingReviews(pageable);
        return ResponseEntity.ok(response);
    }
}
