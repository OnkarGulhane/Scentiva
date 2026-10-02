package com.scentiva.modules.review.service;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.BrandRepository;
import com.scentiva.modules.catalog.repository.CategoryRepository;
import com.scentiva.modules.catalog.repository.ProductRepository;
import com.scentiva.modules.catalog.repository.ProductVariantRepository;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.model.OrderItem;
import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.order.repository.OrderItemRepository;
import com.scentiva.modules.order.repository.OrderRepository;
import com.scentiva.modules.review.dto.ProductReviewSummaryResponse;
import com.scentiva.modules.review.dto.ReviewCreateRequest;
import com.scentiva.modules.review.dto.ReviewModerationRequest;
import com.scentiva.modules.review.dto.ReviewResponse;
import com.scentiva.modules.review.model.ReviewStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private static final String TEST_USER = "review.test@scentiva.luxury";
    private Product product;

    @BeforeEach
    void setUp() {
        User user = userRepository.findByEmailAndIsDeletedFalse(TEST_USER)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(TEST_USER)
                        .passwordHash("hashed")
                        .role(Role.ROLE_CUSTOMER)
                        .build()));

        Customer customer = customerRepository.findByUserEmail(TEST_USER)
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("Reviewer")
                        .lastName("Luxury")
                        .build()));

        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("maison-francis-rev")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Maison Francis Review Svc")
                        .slug("maison-francis-rev")
                        .originCountry("France")
                        .tier(BrandTier.HERITAGE_MAISON)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("amber-rev")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Amber Review Svc")
                        .slug("amber-rev")
                        .build()));

        product = productRepository.findBySlugAndIsDeletedFalse("baccarat-rouge-rev")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Baccarat Rouge Review Svc")
                        .slug("baccarat-rouge-rev")
                        .gender(GenderTarget.UNISEX)
                        .isActive(true)
                        .build()));

        ProductVariant variant = productVariantRepository.findBySkuAndIsDeletedFalse("MFK-BR540-REV-70")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("MFK-BR540-REV-70")
                        .volumeMl(70)
                        .concentration(Concentration.EXTRAIT)
                        .basePrice(new BigDecimal("3800.00"))
                        .salePrice(new BigDecimal("3500.00"))
                        .isActive(true)
                        .build()));

        Order order = orderRepository.save(Order.builder()
                .orderNumber("SC-2026-REV-01")
                .customer(customer)
                .status(OrderStatus.DELIVERED)
                .subtotal(new BigDecimal("3500.00"))
                .discountAmount(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(new BigDecimal("3500.00"))
                .items(new ArrayList<>())
                .build());

        OrderItem item = OrderItem.builder()
                .order(order)
                .variant(variant)
                .sku(variant.getSku())
                .productName(product.getName())
                .variantTitle("70ml Extrait")
                .unitPrice(new BigDecimal("3500.00"))
                .quantity(1)
                .totalPrice(new BigDecimal("3500.00"))
                .build();
        order.addItem(item);
        orderItemRepository.save(item);
    }

    @Test
    @DisplayName("Should submit review and auto-detect verified buyer status")
    void shouldSubmitReviewWithVerifiedPurchase() {
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .productId(product.getId())
                .rating(5)
                .title("Mesmerizing Sillage & Longevity")
                .comment("Incredible ambergris and saffron accords. Lasts over 14 hours.")
                .build();

        ReviewResponse response = reviewService.createReview(TEST_USER, request);

        assertThat(response).isNotNull();
        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.isVerifiedPurchase()).isTrue();
        assertThat(response.getStatus()).isEqualTo(ReviewStatus.APPROVED);

        ProductReviewSummaryResponse summary = reviewService.getProductReviewSummary(product.getId());
        assertThat(summary.getAverageRating()).isEqualTo(5.0);
        assertThat(summary.getTotalReviews()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should moderate review status by admin")
    void shouldModerateReview() {
        ReviewCreateRequest createRequest = ReviewCreateRequest.builder()
                .productId(product.getId())
                .rating(4)
                .title("Great fragrance")
                .comment("Sophisticated dry down")
                .build();

        ReviewResponse created = reviewService.createReview(TEST_USER, createRequest);

        ReviewModerationRequest modRequest = ReviewModerationRequest.builder()
                .status(ReviewStatus.REJECTED)
                .build();

        ReviewResponse moderated = reviewService.moderateReview(created.getId(), modRequest);

        assertThat(moderated.getStatus()).isEqualTo(ReviewStatus.REJECTED);
    }
}
