package com.scentiva.modules.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.cart.dto.AddToCartRequest;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.BrandRepository;
import com.scentiva.modules.catalog.repository.CategoryRepository;
import com.scentiva.modules.catalog.repository.ProductRepository;
import com.scentiva.modules.catalog.repository.ProductVariantRepository;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.inventory.model.InventoryRecord;
import com.scentiva.modules.inventory.model.Warehouse;
import com.scentiva.modules.inventory.repository.InventoryRecordRepository;
import com.scentiva.modules.inventory.repository.WarehouseRepository;
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

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartControllerTest {

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
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private InventoryRecordRepository inventoryRecordRepository;

    private ProductVariant variant;

    @BeforeEach
    void setUp() {
        User user = userRepository.findByEmailAndIsDeletedFalse("cart.user@scentiva.luxury")
                .orElseGet(() -> userRepository.save(User.builder()
                        .email("cart.user@scentiva.luxury")
                        .passwordHash("hashed")
                        .role(Role.ROLE_CUSTOMER)
                        .build()));

        customerRepository.findByUserEmail("cart.user@scentiva.luxury")
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("Cart")
                        .lastName("Tester")
                        .build()));

        Warehouse warehouse = warehouseRepository.findByCodeAndIsDeletedFalse("WH-CART-PUN")
                .orElseGet(() -> warehouseRepository.save(Warehouse.builder()
                        .code("WH-CART-PUN")
                        .name("Pune Hub Cart")
                        .city("Pune")
                        .state("MH")
                        .isActive(true)
                        .build()));

        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("chanel-cart")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Chanel Cart")
                        .slug("chanel-cart")
                        .originCountry("France")
                        .tier(BrandTier.HERITAGE_MAISON)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("floral-cart")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Floral Cart")
                        .slug("floral-cart")
                        .build()));

        Product product = productRepository.findBySlugAndIsDeletedFalse("bleu-de-chanel-cart")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Bleu de Chanel Cart")
                        .slug("bleu-de-chanel-cart")
                        .gender(GenderTarget.FOR_HIM)
                        .isActive(true)
                        .build()));

        variant = productVariantRepository.findBySkuAndIsDeletedFalse("BDC-CART-100")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("BDC-CART-100")
                        .volumeMl(100)
                        .concentration(Concentration.EDP)
                        .basePrice(new BigDecimal("1200.00"))
                        .salePrice(new BigDecimal("1100.00"))
                        .isActive(true)
                        .build()));

        inventoryRecordRepository.findByVariantIdAndWarehouseIdAndIsDeletedFalse(variant.getId(), warehouse.getId())
                .orElseGet(() -> inventoryRecordRepository.save(InventoryRecord.builder()
                        .variant(variant)
                        .warehouse(warehouse)
                        .quantityOnHand(50)
                        .quantityReserved(0)
                        .lowStockThreshold(5)
                        .build()));
    }

    @Test
    @DisplayName("GET /api/v1/cart (Guest session) should return empty cart")
    void shouldGetGuestCart() throws Exception {
        mockMvc.perform(get("/api/v1/cart")
                        .header("X-Session-Id", "guest-session-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalItems", is(0)));
    }

    @Test
    @DisplayName("POST /api/v1/cart/items (Customer) should add item to cart")
    @WithMockUser(username = "cart.user@scentiva.luxury", roles = "CUSTOMER")
    void shouldAddItemToCustomerCart() throws Exception {
        AddToCartRequest request = AddToCartRequest.builder()
                .variantId(variant.getId())
                .quantity(1)
                .build();

        mockMvc.perform(post("/api/v1/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalItems", is(1)))
                .andExpect(jsonPath("$.data.subtotal", is(1100.00)));
    }
}
