package com.scentiva.modules.returns.service;

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
import com.scentiva.modules.returns.dto.ReturnCreateRequest;
import com.scentiva.modules.returns.dto.ReturnItemRequest;
import com.scentiva.modules.returns.dto.ReturnResponse;
import com.scentiva.modules.returns.dto.ReturnStatusUpdateRequest;
import com.scentiva.modules.returns.model.ReturnStatus;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReturnServiceTest {

    @Autowired
    private ReturnService returnService;

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

    private static final String CUSTOMER_EMAIL = "return.tester@scentiva.luxury";
    private Customer customer;
    private ProductVariant variant;
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        User user = userRepository.findByEmailAndIsDeletedFalse(CUSTOMER_EMAIL)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(CUSTOMER_EMAIL)
                        .passwordHash("hashed")
                        .role(Role.ROLE_CUSTOMER)
                        .build()));

        customer = customerRepository.findByUserEmail(CUSTOMER_EMAIL)
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("Return")
                        .lastName("Tester")
                        .build()));

        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("amouage-ret")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Amouage")
                        .slug("amouage-ret")
                        .originCountry("Oman")
                        .tier(BrandTier.NICHE_ATELIER)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("oriental-ret")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Oriental")
                        .slug("oriental-ret")
                        .build()));

        Product product = productRepository.findBySlugAndIsDeletedFalse("interlude-man-ret")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Interlude Man")
                        .slug("interlude-man-ret")
                        .gender(GenderTarget.FOR_HIM)
                        .isActive(true)
                        .build()));

        variant = productVariantRepository.findBySkuAndIsDeletedFalse("AMU-INT-100-RET")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("AMU-INT-100-RET")
                        .volumeMl(100)
                        .concentration(Concentration.EXTRAIT)
                        .basePrice(new BigDecimal("28000.00"))
                        .isActive(true)
                        .build()));

        Warehouse warehouse = warehouseRepository.findByCodeAndIsDeletedFalse("MUM-HUB-RET")
                .orElseGet(() -> warehouseRepository.save(Warehouse.builder()
                        .name("Mumbai Luxury Vault")
                        .code("MUM-HUB-RET")
                        .city("Mumbai")
                        .state("Maharashtra")
                        .build()));

        inventoryRecordRepository.findByVariantIdAndWarehouseIdAndIsDeletedFalse(variant.getId(), warehouse.getId())
                .orElseGet(() -> inventoryRecordRepository.save(InventoryRecord.builder()
                        .warehouse(warehouse)
                        .variant(variant)
                        .quantityOnHand(20)
                        .quantityReserved(0)
                        .build()));

        order = orderRepository.save(Order.builder()
                .customer(customer)
                .orderNumber("ORD-RET-" + System.currentTimeMillis())
                .status(OrderStatus.DELIVERED)
                .subtotal(new BigDecimal("28000.00"))
                .discountAmount(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .taxAmount(new BigDecimal("5040.00"))
                .totalAmount(new BigDecimal("33040.00"))
                .items(new ArrayList<>())
                .build());

        orderItem = orderItemRepository.save(OrderItem.builder()
                .order(order)
                .variant(variant)
                .sku(variant.getSku())
                .productName("Interlude Man")
                .variantTitle("100ml Extrait")
                .unitPrice(new BigDecimal("28000.00"))
                .quantity(1)
                .totalPrice(new BigDecimal("28000.00"))
                .build());
        order.addItem(orderItem);

        paymentRepository.save(Payment.builder()
                .order(order)
                .provider(PaymentProviderType.DEMO)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.SUCCESS)
                .amount(new BigDecimal("33040.00"))
                .currency("INR")
                .gatewayOrderId("TXN-RET-123456")
                .build());
    }

    @Test
    @DisplayName("Should submit return request for delivered order item")
    void shouldCreateReturnRequest() {
        ReturnCreateRequest request = ReturnCreateRequest.builder()
                .orderNumber(order.getOrderNumber())
                .reason("Damaged outer presentation box during luxury transit")
                .items(List.of(ReturnItemRequest.builder()
                        .orderItemId(orderItem.getId())
                        .quantity(1)
                        .reason("Box dented")
                        .build()))
                .build();

        ReturnResponse response = returnService.createReturn(CUSTOMER_EMAIL, request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getReturnNumber()).startsWith("RET-");
        assertThat(response.getStatus()).isEqualTo(ReturnStatus.REQUESTED);
        assertThat(response.getRefundAmount()).isEqualByComparingTo(new BigDecimal("28000.00"));
        assertThat(response.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("Should reject return request if customer is unauthorized")
    void shouldRejectUnauthorizedCustomer() {
        User otherUser = userRepository.save(User.builder()
                .email("other.ret@scentiva.luxury")
                .passwordHash("hashed")
                .role(Role.ROLE_CUSTOMER)
                .build());

        customerRepository.save(Customer.builder()
                .user(otherUser)
                .firstName("Other")
                .lastName("Customer")
                .build());

        ReturnCreateRequest request = ReturnCreateRequest.builder()
                .orderNumber(order.getOrderNumber())
                .reason("Defective atomizer nozzle")
                .items(List.of(ReturnItemRequest.builder()
                        .orderItemId(orderItem.getId())
                        .quantity(1)
                        .build()))
                .build();

        assertThatThrownBy(() -> returnService.createReturn("other.ret@scentiva.luxury", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    @DisplayName("Should transition return status through lifecycle and trigger refund and restock")
    void shouldProcessReturnStatusLifecycleAndRefund() {
        ReturnCreateRequest createRequest = ReturnCreateRequest.builder()
                .orderNumber(order.getOrderNumber())
                .reason("Fragrance scent profile did not match preference")
                .items(List.of(ReturnItemRequest.builder()
                        .orderItemId(orderItem.getId())
                        .quantity(1)
                        .build()))
                .build();

        ReturnResponse returnResp = returnService.createReturn(CUSTOMER_EMAIL, createRequest);

        // 1. Approve return
        ReturnResponse approved = returnService.updateReturnStatus(returnResp.getReturnNumber(),
                ReturnStatusUpdateRequest.builder()
                        .status(ReturnStatus.APPROVED)
                        .adminNotes("Approved for courier concierge pickup")
                        .build());
        assertThat(approved.getStatus()).isEqualTo(ReturnStatus.APPROVED);

        // 2. Mark REFUNDED (should restock and mark order as REFUNDED)
        int initialStock = inventoryRecordRepository.findByVariantIdAndIsDeletedFalse(variant.getId()).get(0).getQuantityOnHand();

        ReturnResponse refunded = returnService.updateReturnStatus(returnResp.getReturnNumber(),
                ReturnStatusUpdateRequest.builder()
                        .status(ReturnStatus.REFUNDED)
                        .adminNotes("Inspected and pristine condition verified")
                        .build());

        assertThat(refunded.getStatus()).isEqualTo(ReturnStatus.REFUNDED);

        // Check inventory incremented
        int updatedStock = inventoryRecordRepository.findByVariantIdAndIsDeletedFalse(variant.getId()).get(0).getQuantityOnHand();
        assertThat(updatedStock).isEqualTo(initialStock + 1);

        // Check order status updated
        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.REFUNDED);
    }

    @Test
    @DisplayName("Should retrieve paginated returns for customer")
    void shouldGetCustomerReturns() {
        ReturnCreateRequest createRequest = ReturnCreateRequest.builder()
                .orderNumber(order.getOrderNumber())
                .reason("Exchanged for different volume")
                .items(List.of(ReturnItemRequest.builder()
                        .orderItemId(orderItem.getId())
                        .quantity(1)
                        .build()))
                .build();

        returnService.createReturn(CUSTOMER_EMAIL, createRequest);

        ApiPaginatedResponse<ReturnResponse> list = returnService.getCustomerReturns(CUSTOMER_EMAIL, PageRequest.of(0, 10));
        assertThat(list.getItems()).isNotEmpty();
        assertThat(list.getTotalElements()).isGreaterThanOrEqualTo(1);
    }
}
