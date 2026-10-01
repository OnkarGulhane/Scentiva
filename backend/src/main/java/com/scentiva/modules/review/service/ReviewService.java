package com.scentiva.modules.review.service;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.review.dto.*;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ApiPaginatedResponse<ReviewResponse> getApprovedReviewsByProduct(Long productId, Pageable pageable);

    ProductReviewSummaryResponse getProductReviewSummary(Long productId);

    ReviewResponse createReview(String email, ReviewCreateRequest request);

    ReviewResponse moderateReview(Long reviewId, ReviewModerationRequest request);

    ApiPaginatedResponse<ReviewResponse> getPendingReviews(Pageable pageable);
}
