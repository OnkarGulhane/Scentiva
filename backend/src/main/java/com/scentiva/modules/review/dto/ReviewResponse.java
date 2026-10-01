package com.scentiva.modules.review.dto;

import com.scentiva.modules.review.model.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long customerId;
    private String customerName;
    private Integer rating;
    private String title;
    private String comment;
    private boolean isVerifiedPurchase;
    private ReviewStatus status;
    private LocalDateTime createdAt;
}
