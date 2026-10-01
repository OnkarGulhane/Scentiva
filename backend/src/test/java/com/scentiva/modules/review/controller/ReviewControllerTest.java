package com.scentiva.modules.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.BrandRepository;
import com.scentiva.modules.catalog.repository.CategoryRepository;
import com.scentiva.modules.catalog.repository.ProductRepository;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.review.dto.ReviewCreateRequest;
import com.scentiva.modules.review.dto.ReviewModerationRequest;
import com.scentiva.modules.review.model.Review;
import com.scentiva.modules.review.model.ReviewStatus;
import com.scentiva.modules.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    private ReviewRepository reviewRepository;

    private static final String USERNAME = "rev.ctrl@scentiva.luxury";
    private Product product;
    private Customer customer;

    @BeforeEach
    void setUp() {
        User user = userRepository.findByEmailAndIsDeletedFalse(USERNAME)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(USERNAME)
                        .passwordHash("hashed")
                        .role(Role.ROLE_CUSTOMER)
                        .build()));

        customer = customerRepository.findByUserEmail(USERNAME)
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("Review")
                        .lastName("Ctrl")
                        .build()));

        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("byredo-rev")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Byredo")
                        .slug("byredo-rev")
                        .originCountry("Sweden")
                        .tier(BrandTier.NICHE_ATELIER)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("fresh-rev")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Fresh")
                        .slug("fresh-rev")
                        .build()));

        product = productRepository.findBySlugAndIsDeletedFalse("gypsy-water-rev")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Gypsy Water")
                        .slug("gypsy-water-rev")
                        .gender(GenderTarget.UNISEX)
                        .isActive(true)
                        .build()));
    }

    @Test
    @DisplayName("POST /api/v1/reviews should submit new customer review")
    @WithMockUser(username = USERNAME, roles = "CUSTOMER")
    void shouldCreateReview() throws Exception {
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .productId(product.getId())
                .rating(5)
                .title("Exquisite pine needle and vanilla notes")
                .comment("Absolute masterpiece. Perfect for intimate evenings.")
                .build();

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.rating", is(5)))
                .andExpect(jsonPath("$.data.productName", is("Gypsy Water")));
    }

    @Test
    @DisplayName("GET /api/v1/reviews/product/{productId} should return approved reviews")
    void shouldGetProductReviews() throws Exception {
        reviewRepository.save(Review.builder()
                .product(product)
                .customer(customer)
                .rating(5)
                .title("Stunning fragrance")
                .comment("Love it!")
                .status(ReviewStatus.APPROVED)
                .build());

        mockMvc.perform(get("/api/v1/reviews/product/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].rating", is(5)));
    }

    @Test
    @DisplayName("PUT /api/v1/reviews/{id}/moderate (Admin) should update review status")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldModerateReviewByAdmin() throws Exception {
        Review review = reviewRepository.save(Review.builder()
                .product(product)
                .customer(customer)
                .rating(4)
                .title("Moderate me")
                .comment("Test comment")
                .status(ReviewStatus.PENDING)
                .build());

        ReviewModerationRequest request = ReviewModerationRequest.builder()
                .status(ReviewStatus.APPROVED)
                .build();

        mockMvc.perform(put("/api/v1/reviews/" + review.getId() + "/moderate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("APPROVED")));
    }
}
