package com.scentiva.stress;

import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.model.UserStatus;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.cart.model.Cart;
import com.scentiva.modules.cart.model.CartItem;
import com.scentiva.modules.cart.repository.CartItemRepository;
import com.scentiva.modules.cart.repository.CartRepository;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.*;
import com.scentiva.modules.checkout.dto.CheckoutProcessRequest;
import com.scentiva.modules.checkout.dto.CheckoutProcessResponse;
import com.scentiva.modules.checkout.service.CheckoutService;
import com.scentiva.modules.customer.model.Address;
import com.scentiva.modules.customer.model.AddressType;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.model.LoyaltyTier;
import com.scentiva.modules.customer.repository.AddressRepository;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.inventory.model.InventoryRecord;
import com.scentiva.modules.inventory.model.Warehouse;
import com.scentiva.modules.inventory.repository.InventoryRecordRepository;
import com.scentiva.modules.inventory.repository.WarehouseRepository;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.repository.OrderRepository;
import com.scentiva.modules.payment.model.PaymentMethod;
import com.scentiva.modules.payment.model.PaymentProviderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CheckoutIdempotencyStressTest {

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

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

    private User user;
    private Customer customer;
    private Address address;
    private ProductVariant variant;

    @BeforeEach
    void setUp() {
        user = userRepository.findByEmailAndIsDeletedFalse("idempotent.stress@scentiva.luxury")
                .orElseGet(() -> userRepository.save(User.builder()
                        .email("idempotent.stress@scentiva.luxury")
                        .passwordHash("$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
                        .role(Role.ROLE_CUSTOMER)
                        .status(UserStatus.ACTIVE)
                        .build()));

        customer = customerRepository.findByUserIdAndIsDeletedFalse(user.getId())
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("Idempotent")
                        .lastName("Tester")
                        .loyaltyTier(LoyaltyTier.BRONZE)
                        .build()));

        address = addressRepository.findByCustomerIdAndIsDefaultTrueAndIsDeletedFalse(customer.getId())
                .orElseGet(() -> addressRepository.save(Address.builder()
                        .customer(customer)
                        .fullName("Idempotent Tester")
                        .phone("+919876543210")
                        .addressLine1("100 Luxury Avenue")
                        .city("Mumbai")
                        .state("Maharashtra")
                        .postalCode("400001")
                        .country("India")
                        .addressType(AddressType.HOME)
                        .isDefault(true)
                        .build()));

        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("amouage-idem-stress")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Amouage Idem Stress")
                        .slug("amouage-idem-stress")
                        .originCountry("Oman")
                        .tier(BrandTier.HERITAGE_MAISON)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("oriental-idem-stress")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Oriental Idem Stress")
                        .slug("oriental-idem-stress")
                        .build()));

        Product product = productRepository.findBySlugAndIsDeletedFalse("interlude-idem-stress")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Interlude 53 Idem Stress")
                        .slug("interlude-idem-stress")
                        .gender(GenderTarget.UNISEX)
                        .isActive(true)
                        .build()));

        variant = productVariantRepository.findBySkuAndIsDeletedFalse("AMO-INT53-100-IDEM")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("AMO-INT53-100-IDEM")
                        .volumeMl(100)
                        .concentration(Concentration.EXTRAIT)
                        .basePrice(new BigDecimal("42000.00"))
                        .isActive(true)
                        .build()));

        Warehouse warehouse = warehouseRepository.findByCodeAndIsDeletedFalse("WH-IDEM-MUM")
                .orElseGet(() -> warehouseRepository.save(Warehouse.builder()
                        .name("Mumbai Vault Idem")
                        .code("WH-IDEM-MUM")
                        .city("Mumbai")
                        .state("Maharashtra")
                        .isActive(true)
                        .build()));

        InventoryRecord record = inventoryRecordRepository
                .findByVariantIdAndWarehouseIdAndIsDeletedFalse(variant.getId(), warehouse.getId())
                .orElseGet(() -> InventoryRecord.builder()
                        .variant(variant)
                        .warehouse(warehouse)
                        .quantityOnHand(100)
                        .quantityReserved(0)
                        .lowStockThreshold(5)
                        .build());
        record.setQuantityOnHand(100);
        record.setQuantityReserved(0);
        inventoryRecordRepository.save(record);

        Cart cart = cartRepository.findByCustomerIdAndIsDeletedFalse(customer.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder()
                        .customer(customer)
                        .items(Collections.emptyList())
                        .build()));

        cartItemRepository.deleteAll(cartItemRepository.findByCartIdAndIsDeletedFalse(cart.getId()));

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .variant(variant)
                .quantity(1)
                .unitPriceAtAddition(variant.getEffectivePrice())
                .build();
        cartItemRepository.save(cartItem);
    }

    @Test
    @DisplayName("Stress Test: 20 concurrent threads using the same idempotency key must create exactly 1 Order and return identical results")
    void shouldEnforceIdempotencyUnderConcurrentSubmissions() throws InterruptedException {
        String sharedIdempotencyKey = "IDEM-KEY-STRESS-" + System.currentTimeMillis();
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        ConcurrentLinkedQueue<CheckoutProcessResponse> responses = new ConcurrentLinkedQueue<>();
        AtomicInteger exceptionCount = new AtomicInteger(0);

        CheckoutProcessRequest request = CheckoutProcessRequest.builder()
                .shippingAddressId(address.getId())
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentProvider(PaymentProviderType.DEMO)
                .simulatedMode("SUCCESS")
                .notes("Concurrent idempotency stress order")
                .build();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    CheckoutProcessResponse response = checkoutService.processCheckout(
                            user.getEmail(),
                            sharedIdempotencyKey,
                            request
                    );
                    responses.add(response);
                } catch (Exception ex) {
                    exceptionCount.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Fire all 20 threads simultaneously
        boolean completed = finishLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(completed).isTrue();
        assertThat(responses).isNotEmpty();

        // Verify that only 1 order exists in database for this idempotency key
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> sharedIdempotencyKey.equals(o.getIdempotencyKey()))
                .toList();

        assertThat(orders).hasSize(1);

        Order singleOrder = orders.getFirst();
        for (CheckoutProcessResponse res : responses) {
            assertThat(res.getOrderId()).isEqualTo(singleOrder.getId());
            assertThat(res.getOrderNumber()).isEqualTo(singleOrder.getOrderNumber());
        }
    }
}
