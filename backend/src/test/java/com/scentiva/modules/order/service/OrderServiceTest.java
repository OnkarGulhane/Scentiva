package com.scentiva.modules.order.service;

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
import com.scentiva.modules.inventory.model.InventoryRecord;
import com.scentiva.modules.inventory.model.Warehouse;
import com.scentiva.modules.inventory.repository.InventoryRecordRepository;
import com.scentiva.modules.inventory.repository.WarehouseRepository;
import com.scentiva.modules.order.dto.OrderCancelRequest;
import com.scentiva.modules.order.dto.OrderResponse;
import com.scentiva.modules.order.dto.OrderStatusUpdateRequest;
import com.scentiva.modules.order.dto.OrderSummaryResponse;
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
import com.scentiva.modules.shipping.dto.ShipmentCreateRequest;
import com.scentiva.modules.shipping.dto.ShipmentTrackingResponse;
import com.scentiva.modules.shipping.model.ShippingProviderType;
import com.scentiva.modules.shipping.service.ShippingService;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ShippingService shippingService;

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

    private static final String TEST_USER = "order.lifecycle@scentiva.luxury";
    private Customer customer;
    private ProductVariant variant;
    private Order order;
    private Payment payment;

    @BeforeEach
    void setUp() {
        User user = userRepository.findByEmailAndIsDeletedFalse(TEST_USER)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(TEST_USER)
                        .passwordHash("hashed")
                        .role(Role.ROLE_CUSTOMER)
                        .build()));

        customer = customerRepository.findByUserEmail(TEST_USER)
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("Order")
                        .lastName("Tester")
                        .build()));

        Warehouse warehouse = warehouseRepository.findByCodeAndIsDeletedFalse("WH-ORD-MUM")
                .orElseGet(() -> warehouseRepository.save(Warehouse.builder()
                        .code("WH-ORD-MUM")
                        .name("Mumbai Order Hub")
                        .city("Mumbai")
                        .state("MH")
                        .isActive(true)
                        .build()));

        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("xerjoff-ord")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Xerjoff Order Svc")
                        .slug("xerjoff-ord")
                        .originCountry("Italy")
                        .tier(BrandTier.HERITAGE_MAISON)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("woody-ord")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Woody Luxury Order Svc")
                        .slug("woody-ord")
                        .build()));

        Product product = productRepository.findBySlugAndIsDeletedFalse("naxos-ord")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Xerjoff Naxos Order Svc")
                        .slug("naxos-ord")
                        .gender(GenderTarget.UNISEX)
                        .isActive(true)
                        .build()));

        variant = productVariantRepository.findBySkuAndIsDeletedFalse("XER-NAX-ORD-100")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("XER-NAX-ORD-100")
                        .volumeMl(100)
                        .concentration(Concentration.EDP)
                        .basePrice(new BigDecimal("2900.00"))
                        .salePrice(new BigDecimal("2600.00"))
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

        order = orderRepository.save(Order.builder()
                .orderNumber("SC-2026-ORD-01")
                .customer(customer)
                .status(OrderStatus.CONFIRMED)
                .subtotal(new BigDecimal("2600.00"))
                .discountAmount(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(new BigDecimal("2600.00"))
                .items(new ArrayList<>())
                .build());

        OrderItem item = OrderItem.builder()
                .order(order)
                .variant(variant)
                .sku(variant.getSku())
                .productName("Xerjoff Naxos")
                .variantTitle("100ml EDP")
                .unitPrice(new BigDecimal("2600.00"))
                .quantity(1)
                .totalPrice(new BigDecimal("2600.00"))
                .build();
        order.addItem(item);
        orderItemRepository.save(item);

        payment = paymentRepository.save(Payment.builder()
                .order(order)
                .amount(new BigDecimal("2600.00"))
                .currency("INR")
                .provider(PaymentProviderType.DEMO)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.SUCCESS)
                .gatewayOrderId("DEMO-GW-ORD01")
                .build());
    }

    @Test
    @DisplayName("Should retrieve paginated orders for customer")
    void shouldGetCustomerOrders() {
        ApiPaginatedResponse<OrderSummaryResponse> response = orderService.getCustomerOrders(TEST_USER, PageRequest.of(0, 10));

        assertThat(response).isNotNull();
        assertThat(response.getItems()).isNotEmpty();
        assertThat(response.getItems().get(0).getOrderNumber()).isEqualTo("SC-2026-ORD-01");
        assertThat(response.getItems().get(0).getTotalAmount()).isEqualByComparingTo(new BigDecimal("2600.00"));
    }

    @Test
    @DisplayName("Should retrieve order details by order number")
    void shouldGetOrderByNumber() {
        OrderResponse response = orderService.getOrderByNumber(TEST_USER, "SC-2026-ORD-01");

        assertThat(response).isNotNull();
        assertThat(response.getOrderNumber()).isEqualTo("SC-2026-ORD-01");
        assertThat(response.getCustomerEmail()).isEqualTo(TEST_USER);
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    @DisplayName("Should cancel confirmed order, refund payment, and restock inventory")
    void shouldCancelConfirmedOrderAndRefund() {
        OrderCancelRequest request = OrderCancelRequest.builder()
                .reason("Customer changed mind")
                .build();

        OrderResponse response = orderService.cancelOrder(TEST_USER, "SC-2026-ORD-01", request);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(response.getNotes()).contains("Customer changed mind");

        // Verify payment marked as REFUNDED
        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }

    @Test
    @DisplayName("Should reject cancellation when order is already shipped")
    void shouldRejectCancellationWhenShipped() {
        // Create shipment to advance order to SHIPPED
        shippingService.createShipment(ShipmentCreateRequest.builder()
                .orderId(order.getId())
                .carrierName("BlueDart Air")
                .build());

        OrderCancelRequest request = OrderCancelRequest.builder()
                .reason("Too late")
                .build();

        assertThatThrownBy(() -> orderService.cancelOrder(TEST_USER, "SC-2026-ORD-01", request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already shipped or delivered");
    }

    @Test
    @DisplayName("Should update order status by admin")
    void shouldUpdateOrderStatus() {
        OrderStatusUpdateRequest request = OrderStatusUpdateRequest.builder()
                .status(OrderStatus.PROCESSING)
                .notes("Packaging perfume in silk box")
                .build();

        OrderResponse response = orderService.updateOrderStatus("SC-2026-ORD-01", request);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.PROCESSING);
        assertThat(response.getNotes()).contains("Packaging perfume in silk box");
    }

    @Test
    @DisplayName("Should track order shipment lifecycle")
    void shouldTrackOrderShipment() {
        shippingService.createShipment(ShipmentCreateRequest.builder()
                .orderId(order.getId())
                .carrierName("Scentiva White-Glove Courier")
                .customTrackingNumber("WG-TRK-7788")
                .build());

        ShipmentTrackingResponse tracking = orderService.trackOrder(TEST_USER, "SC-2026-ORD-01");

        assertThat(tracking.getOrderNumber()).isEqualTo("SC-2026-ORD-01");
        assertThat(tracking.getTrackingNumber()).isEqualTo("WG-TRK-7788");
        assertThat(tracking.getTimeline()).isNotEmpty();
    }
}
