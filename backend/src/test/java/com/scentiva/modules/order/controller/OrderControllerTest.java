package com.scentiva.modules.order.controller;

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
import com.scentiva.modules.inventory.model.InventoryRecord;
import com.scentiva.modules.inventory.model.Warehouse;
import com.scentiva.modules.inventory.repository.InventoryRecordRepository;
import com.scentiva.modules.inventory.repository.WarehouseRepository;
import com.scentiva.modules.order.dto.OrderCancelRequest;
import com.scentiva.modules.order.dto.OrderStatusUpdateRequest;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.model.OrderItem;
import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.order.repository.OrderItemRepository;
import com.scentiva.modules.order.repository.OrderRepository;
import com.scentiva.modules.payment.model.Payment;
import com.scentiva.modules.payment.model.PaymentMethod;
import com.scentiva.modules.payment.model.PaymentProviderType;
import com.scentiva.modules.payment.model.PaymentStatus;
import com.scentiva.modules.payment.repository.PaymentRepository;
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
class OrderControllerTest {

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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private static final String USERNAME = "order.controller.test@scentiva.luxury";
    private Order order;

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
                        .firstName("OrderController")
                        .lastName("Tester")
                        .build()));

        Warehouse warehouse = warehouseRepository.findByCodeAndIsDeletedFalse("WH-ORD-CTRL")
                .orElseGet(() -> warehouseRepository.save(Warehouse.builder()
                        .code("WH-ORD-CTRL")
                        .name("Order Controller Hub")
                        .city("Pune")
                        .state("MH")
                        .isActive(true)
                        .build()));

        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("amouage-ctrl")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Amouage")
                        .slug("amouage-ctrl")
                        .originCountry("Oman")
                        .tier(BrandTier.HERITAGE_MAISON)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("incense-ctrl")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Incense")
                        .slug("incense-ctrl")
                        .build()));

        Product product = productRepository.findBySlugAndIsDeletedFalse("interlude-ctrl")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Interlude Man")
                        .slug("interlude-ctrl")
                        .gender(GenderTarget.FOR_HIM)
                        .isActive(true)
                        .build()));

        ProductVariant variant = productVariantRepository.findBySkuAndIsDeletedFalse("AMO-INT-100")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("AMO-INT-100")
                        .volumeMl(100)
                        .concentration(Concentration.EDP)
                        .basePrice(new BigDecimal("3200.00"))
                        .salePrice(new BigDecimal("2800.00"))
                        .isActive(true)
                        .build()));

        inventoryRecordRepository.findByVariantIdAndWarehouseIdAndIsDeletedFalse(variant.getId(), warehouse.getId())
                .orElseGet(() -> inventoryRecordRepository.save(InventoryRecord.builder()
                        .variant(variant)
                        .warehouse(warehouse)
                        .quantityOnHand(30)
                        .quantityReserved(0)
                        .lowStockThreshold(5)
                        .build()));

        order = orderRepository.save(Order.builder()
                .orderNumber("SC-2026-CTRL-01")
                .customer(customer)
                .status(OrderStatus.CONFIRMED)
                .subtotal(new BigDecimal("2800.00"))
                .discountAmount(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(new BigDecimal("2800.00"))
                .items(new ArrayList<>())
                .build());

        OrderItem item = OrderItem.builder()
                .order(order)
                .variant(variant)
                .sku(variant.getSku())
                .productName("Interlude Man")
                .variantTitle("100ml EDP")
                .unitPrice(new BigDecimal("2800.00"))
                .quantity(1)
                .totalPrice(new BigDecimal("2800.00"))
                .build();
        order.addItem(item);
        orderItemRepository.save(item);

        paymentRepository.save(Payment.builder()
                .order(order)
                .amount(new BigDecimal("2800.00"))
                .currency("INR")
                .provider(PaymentProviderType.DEMO)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.SUCCESS)
                .gatewayOrderId("DEMO-GW-CTRL01")
                .build());
    }

    @Test
    @DisplayName("GET /api/v1/orders should return customer's order history")
    @WithMockUser(username = USERNAME, roles = "CUSTOMER")
    void shouldGetCustomerOrders() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].orderNumber", is("SC-2026-CTRL-01")))
                .andExpect(jsonPath("$.items[0].totalAmount", is(2800.00)));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{orderNumber} should return order details")
    @WithMockUser(username = USERNAME, roles = "CUSTOMER")
    void shouldGetOrderDetails() throws Exception {
        mockMvc.perform(get("/api/v1/orders/SC-2026-CTRL-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.orderNumber", is("SC-2026-CTRL-01")))
                .andExpect(jsonPath("$.data.items[0].productName", is("Interlude Man")));
    }

    @Test
    @DisplayName("POST /api/v1/orders/{orderNumber}/cancel should cancel order")
    @WithMockUser(username = USERNAME, roles = "CUSTOMER")
    void shouldCancelOrder() throws Exception {
        OrderCancelRequest request = OrderCancelRequest.builder()
                .reason("Found alternative fragrance")
                .build();

        mockMvc.perform(post("/api/v1/orders/SC-2026-CTRL-01/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("CANCELLED")));
    }

    @Test
    @DisplayName("PUT /api/v1/orders/{orderNumber}/status (Admin) should advance order state")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldUpdateOrderStatusByAdmin() throws Exception {
        OrderStatusUpdateRequest request = OrderStatusUpdateRequest.builder()
                .status(OrderStatus.PROCESSING)
                .notes("Dispatched to packing atelier")
                .build();

        mockMvc.perform(put("/api/v1/orders/SC-2026-CTRL-01/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("PROCESSING")));
    }

    @Test
    @DisplayName("GET /api/v1/orders/admin/all (Admin) should retrieve all orders")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldGetAllOrdersByAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/orders/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }
}
