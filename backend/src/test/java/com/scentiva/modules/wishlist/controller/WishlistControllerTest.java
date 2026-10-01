package com.scentiva.modules.wishlist.controller;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

    private ProductVariant variant;

    @BeforeEach
    void setUp() {
        User user = userRepository.findByEmailAndIsDeletedFalse("wishlist.user@scentiva.luxury")
                .orElseGet(() -> userRepository.save(User.builder()
                        .email("wishlist.user@scentiva.luxury")
                        .passwordHash("hashed")
                        .role(Role.ROLE_CUSTOMER)
                        .build()));

        customerRepository.findByUserEmail("wishlist.user@scentiva.luxury")
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("Wishlist")
                        .lastName("Tester")
                        .build()));

        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("le-labo-wish")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Le Labo Wish")
                        .slug("le-labo-wish")
                        .originCountry("USA")
                        .tier(BrandTier.NICHE_ATELIER)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("woody-wish")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Woody Wish")
                        .slug("woody-wish")
                        .build()));

        Product product = productRepository.findBySlugAndIsDeletedFalse("santal-33-wish")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Santal 33 Wish")
                        .slug("santal-33-wish")
                        .gender(GenderTarget.UNISEX)
                        .isActive(true)
                        .build()));

        variant = productVariantRepository.findBySkuAndIsDeletedFalse("LL-S33-100-WISH")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("LL-S33-100-WISH")
                        .volumeMl(100)
                        .concentration(Concentration.EDP)
                        .basePrice(new BigDecimal("2800.00"))
                        .salePrice(new BigDecimal("2600.00"))
                        .isActive(true)
                        .build()));
    }

    @Test
    @DisplayName("GET /api/v1/wishlist should return customer wishlist")
    @WithMockUser(username = "wishlist.user@scentiva.luxury", roles = "CUSTOMER")
    void shouldGetWishlist() throws Exception {
        mockMvc.perform(get("/api/v1/wishlist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalCount").isNumber());
    }

    @Test
    @DisplayName("POST /api/v1/wishlist/{variantId} should add fragrance to wishlist")
    @WithMockUser(username = "wishlist.user@scentiva.luxury", roles = "CUSTOMER")
    void shouldAddToWishlist() throws Exception {
        mockMvc.perform(post("/api/v1/wishlist/" + variant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.items[0].sku", is("LL-S33-100-WISH")));
    }
}
