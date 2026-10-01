package com.scentiva.modules.review.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.catalog.model.Product;
import com.scentiva.modules.catalog.repository.ProductRepository;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.order.repository.OrderRepository;
import com.scentiva.modules.review.dto.*;
import com.scentiva.modules.review.model.Review;
import com.scentiva.modules.review.model.ReviewStatus;
import com.scentiva.modules.review.repository.ReviewRepository;
import com.scentiva.modules.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiPaginatedResponse<ReviewResponse> getApprovedReviewsByProduct(Long productId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByProductIdAndStatusAndIsDeletedFalse(productId, ReviewStatus.APPROVED, pageable);

        List<ReviewResponse> items = page.getContent().stream()
                .map(this::mapToReviewResponse)
                .toList();

        return ApiPaginatedResponse.of(items, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductReviewSummaryResponse getProductReviewSummary(Long productId) {
        Double avg = reviewRepository.calculateAverageRatingByProductId(productId);
        Long count = reviewRepository.countApprovedReviewsByProductId(productId);

        double roundedAvg = avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;

        return ProductReviewSummaryResponse.builder()
                .productId(productId)
                .averageRating(roundedAvg)
                .totalReviews(count != null ? count : 0L)
                .build();
    }

    @Override
    @Transactional
    public ReviewResponse createReview(String email, ReviewCreateRequest request) {
        Customer customer = customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));

        Product product = productRepository.findById(request.getProductId())
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        Order order = null;
        if (request.getOrderId() != null) {
            order = orderRepository.findById(request.getOrderId()).orElse(null);
        }

        // Check if customer purchased any variant of this product in a CONFIRMED, SHIPPED or DELIVERED order
        boolean isVerified = false;
        List<Order> customerOrders = orderRepository.findByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(customer.getId());
        for (Order o : customerOrders) {
            if (o.getStatus() == OrderStatus.CONFIRMED || o.getStatus() == OrderStatus.SHIPPED || o.getStatus() == OrderStatus.DELIVERED) {
                boolean hasProduct = o.getItems().stream()
                        .anyMatch(i -> i.getVariant().getProduct().getId().equals(product.getId()));
                if (hasProduct) {
                    isVerified = true;
                    if (order == null) {
                        order = o;
                    }
                    break;
                }
            }
        }

        Review review = Review.builder()
                .product(product)
                .customer(customer)
                .order(order)
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .isVerifiedPurchase(isVerified)
                .status(ReviewStatus.APPROVED) // Auto-approve luxury customer reviews or set to APPROVED
                .build();

        review = reviewRepository.save(review);
        log.info("Created review id={} for product={}, rating={}, verified={}",
                review.getId(), product.getName(), review.getRating(), review.isVerifiedPurchase());

        return mapToReviewResponse(review);
    }

    @Override
    @Transactional
    public ReviewResponse moderateReview(Long reviewId, ReviewModerationRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        review.setStatus(request.getStatus());
        review = reviewRepository.save(review);
        log.info("Moderated review id={} to status={}", review.getId(), review.getStatus());

        return mapToReviewResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiPaginatedResponse<ReviewResponse> getPendingReviews(Pageable pageable) {
        Page<Review> page = reviewRepository.findByStatusAndIsDeletedFalse(ReviewStatus.PENDING, pageable);

        List<ReviewResponse> items = page.getContent().stream()
                .map(this::mapToReviewResponse)
                .toList();

        return ApiPaginatedResponse.of(items, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .customerId(review.getCustomer().getId())
                .customerName(review.getCustomer().getFullName())
                .rating(review.getRating())
                .title(review.getTitle())
                .comment(review.getComment())
                .isVerifiedPurchase(review.isVerifiedPurchase())
                .status(review.getStatus())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
