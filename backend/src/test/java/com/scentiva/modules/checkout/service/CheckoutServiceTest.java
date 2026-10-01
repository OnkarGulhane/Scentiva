package com.scentiva.modules.checkout.service;

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
import com.scentiva.modules.checkout.dto.*;
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
import com.scentiva.modules.payment.model.PaymentMethod;
import com.scentiva.modules.payment.model.PaymentProviderType;
import com.scentiva.modules.payment.model.PaymentStatus;
import com.scentiva.modules.promotion.model.Coupon;
import com.scentiva.modules.promotion.model.DiscountType;
import com.scentiva.modules.promotion.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CheckoutServiceTest {

    @Autowired
    private CheckoutService checkoutService;

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
    private CouponRepository couponRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    private Customer customer;
    private Address address;
    private ProductVariant variant;
    private Warehouse warehouse;

    private static final String TEST_EMAIL = "checkout.tester@scentiva.luxury";

    @BeforeEach
    void setUp() {
        User user = userRepository.findByEmailAndIsDeletedFalse(TEST_EMAIL)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(TEST_EMAIL)
                        .passwordHash("hashed")
                        .role(Role.ROLE_CUSTOMER)
                        .build()));

        customer = customerRepository.findByUserEmail(TEST_EMAIL)
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("Checkout")
                        .lastName("Tester")
                        .phone("+919876543210")
                        .build()));

        address = addressRepository.findByCustomerIdAndIsDefaultTrueAndIsDeletedFalse(customer.getId())
                .orElseGet(() -> addressRepository.save(Address.builder()
                        .customer(customer)
                        .fullName("Checkout Tester")
                        .phone("+919876543210")
                        .addressLine1("123 Luxury Boulevard")
                        .city("Mumbai")
                        .state("MH")
                        .postalCode("400001")
                        .country("India")
                        .addressType(AddressType.HOME)
                        .isDefault(true)
                        .build()));

        warehouse = warehouseRepository.findByCodeAndIsDeletedFalse("WH-CHK-MUM")
                .orElseGet(() -> warehouseRepository.save(Warehouse.builder()
                        .code("WH-CHK-MUM")
                        .name("Mumbai Central Warehouse")
                        .city("Mumbai")
                        .state("MH")
                        .isActive(true)
                        .build()));

        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("roja-checkout")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Roja Parfums")
                        .slug("roja-checkout")
                        .originCountry("United Kingdom")
                        .tier(BrandTier.HERITAGE_MAISON)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("oriental-checkout")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Oriental Luxury")
                        .slug("oriental-checkout")
                        .build()));

        Product product = productRepository.findBySlugAndIsDeletedFalse("elysium-checkout")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Elysium Parfum")
                        .slug("elysium-checkout")
                        .gender(GenderTarget.UNISEX)
                        .isActive(true)
                        .build()));

        variant = productVariantRepository.findBySkuAndIsDeletedFalse("ROJA-ELY-100")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("ROJA-ELY-100")
                        .volumeMl(100)
                        .concentration(Concentration.PARFUM)
                        .basePrice(new BigDecimal("3500.00"))
                        .salePrice(new BigDecimal("3000.00"))
                        .isActive(true)
                        .build()));

        inventoryRecordRepository.findByVariantIdAndWarehouseIdAndIsDeletedFalse(variant.getId(), warehouse.getId())
                .orElseGet(() -> inventoryRecordRepository.save(InventoryRecord.builder()
                        .variant(variant)
                        .warehouse(warehouse)
                        .quantityOnHand(100)
                        .quantityReserved(0)
                        .lowStockThreshold(5)
                        .build()));

        // Add item to cart for test customer
        cartService.clearCart(TEST_EMAIL, null);
        cartService.addToCart(TEST_EMAIL, null, AddToCartRequest.builder()
                .variantId(variant.getId())
                .quantity(1)
                .build());
    }

    @Test
    @DisplayName("Should review checkout calculation accurately with free delivery above threshold")
    void shouldReviewCheckoutCalculation() {
        CheckoutSummaryResponse review = checkoutService.reviewCheckout(TEST_EMAIL, address.getId(), null);

        assertThat(review).isNotNull();
        assertThat(review.getTotalItems()).isEqualTo(1);
        assertThat(review.getItems()).hasSize(1);
        assertThat(review.getSubtotal()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(review.isFreeDelivery()).isTrue();
        assertThat(review.getDeliveryFee()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(review.getTotalAmount()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(review.getShippingAddress()).isNotNull();
        assertThat(review.getShippingAddress().getCity()).isEqualTo("Mumbai");
    }

    @Test
    @DisplayName("Should review checkout with coupon discount applied")
    void shouldReviewCheckoutWithCoupon() {
        couponRepository.save(Coupon.builder()
                .code("ROYAL500")
                .discountType(DiscountType.FIXED_AMOUNT)
                .discountValue(new BigDecimal("500.00"))
                .minOrderValue(new BigDecimal("2000.00"))
                .usageLimitGlobal(100)
                .redemptionCount(0)
                .isActive(true)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build());

        CheckoutSummaryResponse review = checkoutService.reviewCheckout(TEST_EMAIL, address.getId(), "ROYAL500");

        assertThat(review.isCouponApplied()).isTrue();
        assertThat(review.getDiscountAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(review.getTotalAmount()).isEqualByComparingTo(new BigDecimal("2500.00"));
    }

    @Test
    @DisplayName("Should process checkout with instant success payment, confirm order, and deduct inventory")
    void shouldProcessInstantSuccessCheckout() {
        CheckoutProcessRequest request = CheckoutProcessRequest.builder()
                .shippingAddressId(address.getId())
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentProvider(PaymentProviderType.DEMO)
                .simulatedMode("SUCCESS")
                .notes("Luxury gift packaging please")
                .build();

        String idempotencyKey = "IDEM-" + System.currentTimeMillis();

        CheckoutProcessResponse response = checkoutService.processCheckout(TEST_EMAIL, idempotencyKey, request);

        assertThat(response).isNotNull();
        assertThat(response.getOrderNumber()).startsWith("SC-");
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(response.isRequiresAction()).isFalse();

        // Verify order saved in database
        Order savedOrder = orderRepository.findById(response.getOrderId()).orElseThrow();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(savedOrder.getSnapshot()).isNotNull();
        assertThat(savedOrder.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("Should enforce idempotency by returning existing order without double charging")
    void shouldEnforceIdempotency() {
        CheckoutProcessRequest request = CheckoutProcessRequest.builder()
                .shippingAddressId(address.getId())
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentProvider(PaymentProviderType.DEMO)
                .simulatedMode("SUCCESS")
                .build();

        String idempotencyKey = "IDEM-REPEAT-KEY-12345";

        CheckoutProcessResponse response1 = checkoutService.processCheckout(TEST_EMAIL, idempotencyKey, request);
        CheckoutProcessResponse response2 = checkoutService.processCheckout(TEST_EMAIL, idempotencyKey, request);

        assertThat(response1.getOrderId()).isEqualTo(response2.getOrderId());
        assertThat(response1.getOrderNumber()).isEqualTo(response2.getOrderNumber());
    }

    @Test
    @DisplayName("Should process 3DS requiring action flow and verify successfully")
    void shouldProcessAndVerify3DSPayment() {
        CheckoutProcessRequest request = CheckoutProcessRequest.builder()
                .shippingAddressId(address.getId())
                .paymentMethod(PaymentMethod.NET_BANKING)
                .paymentProvider(PaymentProviderType.DEMO)
                .simulatedMode("REQUIRES_ACTION")
                .build();

        CheckoutProcessResponse processResponse = checkoutService.processCheckout(TEST_EMAIL, "IDEM-3DS-" + System.currentTimeMillis(), request);

        assertThat(processResponse.getOrderStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(processResponse.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(processResponse.isRequiresAction()).isTrue();
        assertThat(processResponse.getActionUrl()).isNotBlank();

        // Now verify with valid OTP
        CheckoutVerifyRequest verifyRequest = CheckoutVerifyRequest.builder()
                .orderNumber(processResponse.getOrderNumber())
                .gatewayPaymentId("PAY-3DS-GATEWAY-123")
                .otp("123456")
                .build();

        CheckoutVerifyResponse verifyResponse = checkoutService.verifyCheckout(TEST_EMAIL, verifyRequest);

        assertThat(verifyResponse.isSuccess()).isTrue();
        assertThat(verifyResponse.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(verifyResponse.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    @DisplayName("Should cancel order and release inventory hold when verification fails")
    void shouldCancelOrderOnVerificationFailure() {
        CheckoutProcessRequest request = CheckoutProcessRequest.builder()
                .shippingAddressId(address.getId())
                .paymentMethod(PaymentMethod.NET_BANKING)
                .paymentProvider(PaymentProviderType.DEMO)
                .simulatedMode("REQUIRES_ACTION")
                .build();

        CheckoutProcessResponse processResponse = checkoutService.processCheckout(TEST_EMAIL, "IDEM-3DS-FAIL-" + System.currentTimeMillis(), request);

        // Verify with invalid OTP
        CheckoutVerifyRequest verifyRequest = CheckoutVerifyRequest.builder()
                .orderNumber(processResponse.getOrderNumber())
                .gatewayPaymentId("PAY-3DS-GATEWAY-123")
                .otp("INVALID")
                .build();

        CheckoutVerifyResponse verifyResponse = checkoutService.verifyCheckout(TEST_EMAIL, verifyRequest);

        assertThat(verifyResponse.isSuccess()).isFalse();
        assertThat(verifyResponse.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(verifyResponse.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
    }
}
