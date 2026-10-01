package com.scentiva.modules.checkout.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.cart.dto.AddToCartRequest;
import com.scentiva.modules.cart.service.CartService;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.BrandRepository;
import com.scentiva.modules.catalog.repository.CategoryRepository;
import com.scentiva.modules.catalog.repository.ProductRepository;
import com.scentiva.modules.catalog.repository.ProductVariantRepository;
import com.scentiva.modules.checkout.dto.CheckoutProcessRequest;
import com.scentiva.modules.checkout.dto.CheckoutVerifyRequest;
import com.scentiva.modules.customer.model.Address;
import com.scentiva.modules.customer.model.AddressType;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.AddressRepository;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.inventory.model.InventoryRecord;
import com.scentiva.modules.inventory.model.Warehouse;
import com.scentiva.modules.inventory.repository.InventoryRecordRepository;
import com.scentiva.modules.inventory.repository.WarehouseRepository;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.model.OrderStatus;
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

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

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
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private static final String USERNAME = "checkout.api@scentiva.luxury";
    private Address address;
    private ProductVariant variant;

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
                        .firstName("API")
                        .lastName("Tester")
                        .phone("+919999988888")
                        .build()));

        address = addressRepository.findByCustomerIdAndIsDefaultTrueAndIsDeletedFalse(customer.getId())
                .orElseGet(() -> addressRepository.save(Address.builder()
                        .customer(customer)
                        .fullName("API Tester")
                        .phone("+919999988888")
                        .addressLine1("456 Luxury Towers")
                        .city("Delhi")
                        .state("DL")
                        .postalCode("110001")
                        .country("India")
                        .addressType(AddressType.HOME)
                        .isDefault(true)
                        .build()));

        Warehouse warehouse = warehouseRepository.findByCodeAndIsDeletedFalse("WH-CHK-DEL")
                .orElseGet(() -> warehouseRepository.save(Warehouse.builder()
                        .code("WH-CHK-DEL")
                        .name("Delhi Hub")
                        .city("Delhi")
                        .state("DL")
                        .isActive(true)
                        .build()));

        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("creed-chk")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Creed Checkout")
                        .slug("creed-chk")
                        .originCountry("France")
                        .tier(BrandTier.HERITAGE_MAISON)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("fresh-chk")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Fresh Checkout")
                        .slug("fresh-chk")
                        .build()));

        Product product = productRepository.findBySlugAndIsDeletedFalse("aventus-chk")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Aventus Checkout")
                        .slug("aventus-chk")
                        .gender(GenderTarget.FOR_HIM)
                        .isActive(true)
                        .build()));

        variant = productVariantRepository.findBySkuAndIsDeletedFalse("CREED-AV-100")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("CREED-AV-100")
                        .volumeMl(100)
                        .concentration(Concentration.EDP)
                        .basePrice(new BigDecimal("2800.00"))
                        .salePrice(new BigDecimal("2500.00"))
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

        cartService.clearCart(USERNAME, null);
        cartService.addToCart(USERNAME, null, AddToCartRequest.builder()
                .variantId(variant.getId())
                .quantity(1)
                .build());
    }

    @Test
    @DisplayName("GET /api/v1/checkout/review should return pre-flight checkout breakdown")
    @WithMockUser(username = USERNAME, roles = "CUSTOMER")
    void shouldReviewCheckout() throws Exception {
        mockMvc.perform(get("/api/v1/checkout/review")
                        .param("shippingAddressId", address.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalItems", is(1)))
                .andExpect(jsonPath("$.data.subtotal", is(2500.00)))
                .andExpect(jsonPath("$.data.totalAmount", is(2500.00)))
                .andExpect(jsonPath("$.data.shippingAddress.city", is("Delhi")));
    }

    @Test
    @DisplayName("POST /api/v1/checkout/process should place order and initialize payment")
    @WithMockUser(username = USERNAME, roles = "CUSTOMER")
    void shouldProcessCheckout() throws Exception {
        CheckoutProcessRequest request = CheckoutProcessRequest.builder()
                .shippingAddressId(address.getId())
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentProvider(PaymentProviderType.DEMO)
                .simulatedMode("SUCCESS")
                .notes("Handle with care")
                .build();

        mockMvc.perform(post("/api/v1/checkout/process")
                        .header("Idempotency-Key", "IDEM-" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.orderStatus", is("CONFIRMED")))
                .andExpect(jsonPath("$.data.paymentStatus", is("SUCCESS")))
                .andExpect(jsonPath("$.data.totalAmount", is(2500.00)));
    }

    @Test
    @DisplayName("POST /api/v1/checkout/verify should verify OTP and complete order")
    @WithMockUser(username = USERNAME, roles = "CUSTOMER")
    void shouldVerifyCheckoutPayment() throws Exception {
        // Pre-create an order and payment
        Customer customer = customerRepository.findByUserEmail(USERNAME).orElseThrow();
        Order order = orderRepository.save(Order.builder()
                .orderNumber("SC-2026-VERIFY-001")
                .customer(customer)
                .status(OrderStatus.PLACED)
                .subtotal(new BigDecimal("2500.00"))
                .discountAmount(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(new BigDecimal("2500.00"))
                .build());

        paymentRepository.save(Payment.builder()
                .order(order)
                .amount(new BigDecimal("2500.00"))
                .currency("INR")
                .provider(PaymentProviderType.DEMO)
                .paymentMethod(PaymentMethod.NET_BANKING)
                .status(PaymentStatus.PENDING)
                .gatewayOrderId("GW-ORD-12345")
                .build());

        CheckoutVerifyRequest request = CheckoutVerifyRequest.builder()
                .orderNumber("SC-2026-VERIFY-001")
                .gatewayPaymentId("PAY-DEMO-VERIFY")
                .otp("123456")
                .build();

        mockMvc.perform(post("/api/v1/checkout/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.orderStatus", is("CONFIRMED")))
                .andExpect(jsonPath("$.data.paymentStatus", is("SUCCESS")));
    }
}
