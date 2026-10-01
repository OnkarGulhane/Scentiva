package com.scentiva.modules.returns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.scentiva.modules.returns.dto.ReturnCreateRequest;
import com.scentiva.modules.returns.dto.ReturnItemRequest;
import com.scentiva.modules.returns.dto.ReturnStatusUpdateRequest;
import com.scentiva.modules.returns.model.ReturnRequest;
import com.scentiva.modules.returns.model.ReturnStatus;
import com.scentiva.modules.returns.repository.ReturnRepository;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReturnControllerTest {

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
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ReturnRepository returnRepository;

    private static final String USERNAME = "ret.controller@scentiva.luxury";
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        User user = userRepository.findByEmailAndIsDeletedFalse(USERNAME)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(USERNAME)
                        .passwordHash("hashed")
                        .role(Role.ROLE_CUSTOMER)
                        .build()));

        Customer customer = customerRepository.findByUserEmail(USERNAME)
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("Return")
                        .lastName("Controller")
                        .build()));

        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("maison-ret-ctrl")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Maison Francis")
                        .slug("maison-ret-ctrl")
                        .originCountry("France")
                        .tier(BrandTier.NICHE_ATELIER)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("woody-ret-ctrl")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Woody")
                        .slug("woody-ret-ctrl")
                        .build()));

        Product product = productRepository.findBySlugAndIsDeletedFalse("baccarat-ret-ctrl")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Baccarat Rouge 540")
                        .slug("baccarat-ret-ctrl")
                        .gender(GenderTarget.UNISEX)
                        .isActive(true)
                        .build()));

        ProductVariant variant = productVariantRepository.findBySkuAndIsDeletedFalse("MFK-BAC-70-RET")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("MFK-BAC-70-RET")
                        .volumeMl(70)
                        .concentration(Concentration.EXTRAIT)
                        .basePrice(new BigDecimal("35000.00"))
                        .isActive(true)
                        .build()));

        order = orderRepository.save(Order.builder()
                .customer(customer)
                .orderNumber("ORD-RET-CTRL-" + System.currentTimeMillis())
                .status(OrderStatus.DELIVERED)
                .subtotal(new BigDecimal("35000.00"))
                .discountAmount(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .taxAmount(new BigDecimal("6300.00"))
                .totalAmount(new BigDecimal("41300.00"))
                .items(new ArrayList<>())
                .build());

        orderItem = orderItemRepository.save(OrderItem.builder()
                .order(order)
                .variant(variant)
                .sku(variant.getSku())
                .productName("Baccarat Rouge 540")
                .variantTitle("70ml Extrait")
                .unitPrice(new BigDecimal("35000.00"))
                .quantity(1)
                .totalPrice(new BigDecimal("35000.00"))
                .build());
        order.addItem(orderItem);
    }

    @Test
    @DisplayName("POST /api/v1/returns should submit customer return request")
    @WithMockUser(username = USERNAME, roles = "CUSTOMER")
    void shouldCreateReturnRequest() throws Exception {
        ReturnCreateRequest request = ReturnCreateRequest.builder()
                .orderNumber(order.getOrderNumber())
                .reason("Atomizer spray mechanism jammed upon unboxing")
                .items(List.of(ReturnItemRequest.builder()
                        .orderItemId(orderItem.getId())
                        .quantity(1)
                        .reason("Defective atomizer")
                        .build()))
                .build();

        mockMvc.perform(post("/api/v1/returns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.returnNumber", startsWith("RET-")))
                .andExpect(jsonPath("$.data.status", is("REQUESTED")))
                .andExpect(jsonPath("$.data.refundAmount", is(35000.0)));
    }

    @Test
    @DisplayName("GET /api/v1/returns should return customer's paginated return requests")
    @WithMockUser(username = USERNAME, roles = "CUSTOMER")
    void shouldGetCustomerReturns() throws Exception {
        mockMvc.perform(get("/api/v1/returns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    @DisplayName("PUT /api/v1/returns/{returnNumber}/status should update return status by ADMIN")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldUpdateReturnStatusByAdmin() throws Exception {
        Customer customer = customerRepository.findByUserEmail(USERNAME).orElseThrow();
        ReturnRequest returnReq = returnRepository.save(ReturnRequest.builder()
                .customer(customer)
                .order(order)
                .returnNumber("RET-2026-TEST001")
                .status(ReturnStatus.REQUESTED)
                .reason("Minor leak")
                .refundAmount(new BigDecimal("35000.00"))
                .build());

        ReturnStatusUpdateRequest updateRequest = ReturnStatusUpdateRequest.builder()
                .status(ReturnStatus.APPROVED)
                .pickupTrackingNumber("TRACK-DHL-RET-999")
                .adminNotes("Courier scheduled for luxury pickup")
                .build();

        mockMvc.perform(put("/api/v1/returns/RET-2026-TEST001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("APPROVED")))
                .andExpect(jsonPath("$.data.pickupTrackingNumber", is("TRACK-DHL-RET-999")));
    }
}
