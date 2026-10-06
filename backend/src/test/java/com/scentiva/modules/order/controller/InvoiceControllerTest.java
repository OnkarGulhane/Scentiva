package com.scentiva.modules.order.controller;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class InvoiceControllerTest {

    private static final String TEST_USER_EMAIL = "inv.ctrl.cust@scentiva.test";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

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

    private User customerUser;
    private Customer customer;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        customerUser = userRepository.findByEmailAndIsDeletedFalse(TEST_USER_EMAIL)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(TEST_USER_EMAIL)
                        .passwordHash("$2a$12$eX4mP1eHashForTestingOnlySecure1234567890")
                        .role(Role.ROLE_CUSTOMER)
                        .build()));

        customer = customerRepository.findByUserEmail(TEST_USER_EMAIL)
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(customerUser)
                        .firstName("Isabella")
                        .lastName("Vane")
                        .phone("+919112233445")
                        .build()));

        Brand brand = brandRepository.findByNameAndIsDeletedFalse("Creed")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Creed")
                        .slug("creed-inv-ctrl-" + System.currentTimeMillis())
                        .originCountry("France")
                        .tier(BrandTier.HERITAGE_MAISON)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("edp-inv-ctrl")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Eau de Parfum")
                        .slug("edp-inv-ctrl")
                        .build()));

        Product product = productRepository.findBySlugAndIsDeletedFalse("aventus-inv-ctrl")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Aventus")
                        .slug("aventus-inv-ctrl")
                        .gender(GenderTarget.UNISEX)
                        .isActive(true)
                        .build()));

        ProductVariant variant = productVariantRepository.findBySkuAndIsDeletedFalse("CRD-AVN-100ML-INV")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("CRD-AVN-100ML-INV")
                        .volumeMl(100)
                        .concentration(Concentration.EDP)
                        .basePrice(new BigDecimal("32000.00"))
                        .salePrice(new BigDecimal("32000.00"))
                        .isActive(true)
                        .build()));

        testOrder = Order.builder()
                .customer(customer)
                .orderNumber("ORD-INV-CTRL-" + System.currentTimeMillis())
                .status(OrderStatus.CONFIRMED)
                .subtotal(new BigDecimal("32000.00"))
                .discountAmount(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .taxAmount(new BigDecimal("5760.00"))
                .totalAmount(new BigDecimal("37760.00"))
                .items(new ArrayList<>())
                .build();

        testOrder = orderRepository.save(testOrder);

        OrderItem item = OrderItem.builder()
                .order(testOrder)
                .variant(variant)
                .sku(variant.getSku())
                .productName(product.getName())
                .variantTitle("100ml EDP")
                .unitPrice(variant.getBasePrice())
                .quantity(1)
                .totalPrice(variant.getBasePrice())
                .build();

        testOrder.getItems().add(orderItemRepository.save(item));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{orderNumber}/invoice returns 200 JSON with invoice payload")
    @WithMockUser(username = TEST_USER_EMAIL, roles = {"CUSTOMER"})
    void testGetInvoiceJson_Success() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{orderNumber}/invoice", testOrder.getOrderNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderNumber", is(testOrder.getOrderNumber())))
                .andExpect(jsonPath("$.data.invoiceNumber", notNullValue()))
                .andExpect(jsonPath("$.data.customerName", is("Isabella Vane")))
                .andExpect(jsonPath("$.data.grandTotal", is(37760.00)))
                .andExpect(jsonPath("$.data.items", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{orderNumber}/invoice/pdf returns 200 with application/pdf Content-Type")
    @WithMockUser(username = TEST_USER_EMAIL, roles = {"CUSTOMER"})
    void testGetInvoicePdf_Success() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{orderNumber}/invoice/pdf", testOrder.getOrderNumber()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("inline; filename=\"Scentiva-Invoice-")));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{orderNumber}/invoice denies access to unauthorized customer (403)")
    @WithMockUser(username = "unauthorized.user@scentiva.test", roles = {"CUSTOMER"})
    void testGetInvoiceJson_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{orderNumber}/invoice", testOrder.getOrderNumber()))
                .andExpect(status().isForbidden());
    }
}
