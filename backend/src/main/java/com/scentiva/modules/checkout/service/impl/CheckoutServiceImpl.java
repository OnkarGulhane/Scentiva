package com.scentiva.modules.checkout.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.cart.model.Cart;
import com.scentiva.modules.cart.model.CartItem;
import com.scentiva.modules.cart.repository.CartItemRepository;
import com.scentiva.modules.cart.repository.CartRepository;
import com.scentiva.modules.cart.service.CartService;
import com.scentiva.modules.catalog.model.Product;
import com.scentiva.modules.catalog.model.ProductImage;
import com.scentiva.modules.catalog.model.ProductVariant;
import com.scentiva.modules.checkout.dto.*;
import com.scentiva.modules.checkout.service.CheckoutService;
import com.scentiva.modules.customer.dto.AddressResponse;
import com.scentiva.modules.customer.model.Address;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.AddressRepository;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.inventory.dto.StockReservationRequest;
import com.scentiva.modules.inventory.service.InventoryService;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.model.OrderItem;
import com.scentiva.modules.order.model.OrderSnapshot;
import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.order.repository.OrderItemRepository;
import com.scentiva.modules.order.repository.OrderRepository;
import com.scentiva.modules.order.repository.OrderSnapshotRepository;
import com.scentiva.modules.payment.model.Payment;
import com.scentiva.modules.payment.model.PaymentProviderType;
import com.scentiva.modules.payment.model.PaymentStatus;
import com.scentiva.modules.payment.provider.PaymentInitResult;
import com.scentiva.modules.payment.provider.PaymentVerifyResult;
import com.scentiva.modules.payment.repository.PaymentRepository;
import com.scentiva.modules.payment.service.PaymentService;
import com.scentiva.modules.promotion.dto.CouponValidationResponse;
import com.scentiva.modules.promotion.model.DiscountType;
import com.scentiva.modules.promotion.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("2000.00");
    private static final BigDecimal STANDARD_DELIVERY_FEE = new BigDecimal("150.00");

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final InventoryService inventoryService;
    private final CouponService couponService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderSnapshotRepository orderSnapshotRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;
    private final com.scentiva.modules.notification.service.NotificationService notificationService;

    @org.springframework.beans.factory.annotation.Value("${scentiva.razorpay.key-id:rzp_test_TkFZU8ecNzFnCq}")
    private String razorpayKeyId;

    @Override
    @Transactional(readOnly = true)
    public CheckoutSummaryResponse reviewCheckout(String email, Long shippingAddressId, String couponCode) {
        Customer customer = getCustomerByEmail(email);

        Address address = null;
        if (shippingAddressId != null) {
            address = addressRepository.findByIdAndCustomerIdAndIsDeletedFalse(shippingAddressId, customer.getId())
                    .orElse(null);
        }
        if (address == null) {
            address = addressRepository.findByCustomerIdAndIsDefaultTrueAndIsDeletedFalse(customer.getId())
                    .orElse(null);
        }

        Cart cart = cartRepository.findByCustomerIdAndIsDeletedFalse(customer.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart is empty"));

        List<CartItem> items = cartItemRepository.findByCartIdAndIsDeletedFalse(cart.getId());
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Shopping cart is empty");
        }

        List<CheckoutItemSummary> itemSummaries = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : items) {
            ProductVariant variant = item.getVariant();
            Product product = variant.getProduct();
            BigDecimal unitPrice = variant.getEffectivePrice();
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())).setScale(2, RoundingMode.HALF_UP);
            subtotal = subtotal.add(totalPrice);
            totalItems += item.getQuantity();

            String imageUrl = (product != null && product.getImages() != null && !product.getImages().isEmpty())
                    ? product.getImages().get(0).getImageUrl()
                    : null;

            itemSummaries.add(CheckoutItemSummary.builder()
                    .variantId(variant.getId())
                    .sku(variant.getSku())
                    .productName(product != null ? product.getName() : null)
                    .brandName(product != null && product.getBrand() != null ? product.getBrand().getName() : null)
                    .concentration(variant.getConcentration())
                    .volumeMl(variant.getVolumeMl())
                    .imageUrl(imageUrl)
                    .unitPrice(unitPrice)
                    .quantity(item.getQuantity())
                    .totalPrice(totalPrice)
                    .build());
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        CouponValidationResponse couponResult = (couponCode != null && !couponCode.trim().isEmpty())
                ? couponService.validateAndCalculateDiscount(couponCode.trim(), subtotal)
                : null;

        BigDecimal discountAmount = (couponResult != null && couponResult.isValid())
                ? couponResult.getCalculatedDiscount()
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        boolean isFreeDelivery = subtotal.compareTo(FREE_DELIVERY_THRESHOLD) >= 0 ||
                (couponResult != null && couponResult.isValid() && couponResult.getDiscountType() == DiscountType.FREE_SHIPPING);

        BigDecimal deliveryFee = isFreeDelivery
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : STANDARD_DELIVERY_FEE.setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(deliveryFee).add(taxAmount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        return CheckoutSummaryResponse.builder()
                .items(itemSummaries)
                .shippingAddress(address != null ? mapToAddressResponse(address) : null)
                .totalItems(totalItems)
                .subtotal(subtotal)
                .couponCode(couponResult != null && couponResult.isValid() ? couponResult.getCode() : null)
                .isCouponApplied(couponResult != null && couponResult.isValid())
                .discountAmount(discountAmount)
                .deliveryFee(deliveryFee)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .isFreeDelivery(isFreeDelivery)
                .build();
    }

    @Override
    @Transactional
    public CheckoutProcessResponse processCheckout(String email, String idempotencyKey, CheckoutProcessRequest request) {
        // 1. Idempotency Check
        if (idempotencyKey != null && !idempotencyKey.trim().isEmpty()) {
            Optional<Order> existingOrderOpt = orderRepository.findByIdempotencyKey(idempotencyKey.trim());
            if (existingOrderOpt.isPresent()) {
                Order existingOrder = existingOrderOpt.get();
                log.info("Idempotent request detected. Returning existing order={}", existingOrder.getOrderNumber());
                List<Payment> payments = paymentRepository.findByOrderId(existingOrder.getId());
                Payment latestPayment = payments.isEmpty() ? null : payments.get(payments.size() - 1);

                return CheckoutProcessResponse.builder()
                        .orderId(existingOrder.getId())
                        .orderNumber(existingOrder.getOrderNumber())
                        .orderStatus(existingOrder.getStatus())
                        .totalAmount(existingOrder.getTotalAmount())
                        .paymentId(latestPayment != null ? latestPayment.getId() : null)
                        .paymentStatus(latestPayment != null ? latestPayment.getStatus() : PaymentStatus.PENDING)
                        .gatewayOrderId(latestPayment != null ? latestPayment.getGatewayOrderId() : null)
                        .requiresAction(false)
                        .message("Order retrieved from idempotent key")
                        .build();
            }
        }

        Customer customer = getCustomerByEmail(email);

        // 2. Validate Address
        Address address = addressRepository.findByIdAndCustomerIdAndIsDeletedFalse(request.getShippingAddressId(), customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", request.getShippingAddressId()));

        // 3. Validate Cart
        Cart cart = cartRepository.findByCustomerIdAndIsDeletedFalse(customer.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart is empty"));

        List<CartItem> cartItems = cartItemRepository.findByCartIdAndIsDeletedFalse(cart.getId());
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Shopping cart is empty");
        }

        String orderNumber = "SC-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 4. Reserve stock for 15 minutes hold
        for (CartItem item : cartItems) {
            ProductVariant variant = item.getVariant();
            inventoryService.reserveStock(StockReservationRequest.builder()
                    .variantId(variant.getId())
                    .quantity(item.getQuantity())
                    .referenceId(orderNumber)
                    .build());
        }

        // 5. Calculate Financials
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            BigDecimal lineTotal = item.getVariant().getEffectivePrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        CouponValidationResponse couponResult = (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty())
                ? couponService.validateAndCalculateDiscount(request.getCouponCode().trim(), subtotal)
                : null;

        BigDecimal discountAmount = (couponResult != null && couponResult.isValid())
                ? couponResult.getCalculatedDiscount()
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        boolean isFreeDelivery = subtotal.compareTo(FREE_DELIVERY_THRESHOLD) >= 0 ||
                (couponResult != null && couponResult.isValid() && couponResult.getDiscountType() == DiscountType.FREE_SHIPPING);

        BigDecimal deliveryFee = isFreeDelivery
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : STANDARD_DELIVERY_FEE.setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(deliveryFee).add(taxAmount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        // 6. Create Order Entity
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customer(customer)
                .status(OrderStatus.PLACED)
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .deliveryFee(deliveryFee)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .idempotencyKey(idempotencyKey != null ? idempotencyKey.trim() : null)
                .notes(request.getNotes())
                .items(new ArrayList<>())
                .build();

        order = orderRepository.save(order);

        // 7. Create Order Items
        for (CartItem item : cartItems) {
            ProductVariant variant = item.getVariant();
            Product product = variant.getProduct();
            BigDecimal unitPrice = variant.getEffectivePrice();
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())).setScale(2, RoundingMode.HALF_UP);

            String variantTitle = variant.getVolumeMl() + "ml " + variant.getConcentration();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .variant(variant)
                    .sku(variant.getSku())
                    .productName(product != null ? product.getName() : "Fragrance")
                    .variantTitle(variantTitle)
                    .unitPrice(unitPrice)
                    .quantity(item.getQuantity())
                    .totalPrice(totalPrice)
                    .build();

            order.addItem(orderItem);
            orderItemRepository.save(orderItem);
        }

        // 8. Create Order Snapshot
        try {
            Map<String, Object> customerSnapshot = Map.of(
                    "customerId", customer.getId(),
                    "email", customer.getUser().getEmail(),
                    "fullName", customer.getFullName(),
                    "phone", customer.getPhone() != null ? customer.getPhone() : ""
            );

            Map<String, Object> addressSnapshot = Map.of(
                    "addressId", address.getId(),
                    "fullName", address.getFullName(),
                    "phone", address.getPhone(),
                    "addressLine1", address.getAddressLine1(),
                    "city", address.getCity(),
                    "state", address.getState(),
                    "postalCode", address.getPostalCode(),
                    "country", address.getCountry()
            );

            Map<String, Object> pricingSnapshot = Map.of(
                    "subtotal", subtotal,
                    "discountAmount", discountAmount,
                    "couponCode", couponResult != null && couponResult.isValid() ? couponResult.getCode() : "",
                    "deliveryFee", deliveryFee,
                    "taxAmount", taxAmount,
                    "totalAmount", totalAmount
            );

            OrderSnapshot snapshot = OrderSnapshot.builder()
                    .order(order)
                    .customerSnapshotJson(objectMapper.writeValueAsString(customerSnapshot))
                    .shippingAddressSnapshotJson(objectMapper.writeValueAsString(addressSnapshot))
                    .pricingMatrixSnapshotJson(objectMapper.writeValueAsString(pricingSnapshot))
                    .build();

            orderSnapshotRepository.save(snapshot);
            order.setSnapshot(snapshot);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize order snapshot JSON", e);
        }

        // 9. Process Payment
        PaymentInitResult paymentResult = paymentService.processOrderPayment(
                order,
                request.getPaymentMethod(),
                request.getPaymentProvider(),
                request.getSimulatedMode()
        );

        // 10. If Instant Payment Success: finalize order and deduct stock
        if (paymentResult.getStatus() == PaymentStatus.SUCCESS) {
            order.setStatus(OrderStatus.CONFIRMED);
            order = orderRepository.save(order);

            for (CartItem item : cartItems) {
                // Permanently deduct stock
                inventoryService.deductStock(item.getVariant().getId(), null, item.getQuantity(), orderNumber);
            }

            if (couponResult != null && couponResult.isValid()) {
                couponService.recordRedemption(couponResult.getCode());
            }

            cartService.clearCart(email, null);

            try {
                notificationService.handleOrderNotification(com.scentiva.modules.notification.event.OrderNotificationEvent.builder()
                        .customerId(customer.getId())
                        .customerEmail(email)
                        .customerName(customer.getFirstName())
                        .orderNumber(order.getOrderNumber())
                        .eventType("CONFIRMED")
                        .totalAmount(order.getTotalAmount())
                        .build());
            } catch (Exception e) {
                log.warn("Failed to dispatch order confirmation notification for order={}: {}", order.getOrderNumber(), e.getMessage());
            }
        }

        log.info("Processed checkout for order={}, status={}, totalAmount={}", order.getOrderNumber(), order.getStatus(), order.getTotalAmount());

        List<Payment> payments = paymentRepository.findByOrderId(order.getId());
        Payment payment = payments.isEmpty() ? null : payments.get(0);

        return CheckoutProcessResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .paymentId(payment != null ? payment.getId() : null)
                .paymentStatus(paymentResult.getStatus())
                .gatewayOrderId(paymentResult.getGatewayOrderId())
                .keyId(request.getPaymentProvider() == PaymentProviderType.RAZORPAY ? razorpayKeyId : null)
                .requiresAction(paymentResult.isRequiresAction())
                .actionUrl(paymentResult.getActionUrl())
                .message(paymentResult.getMessage())
                .build();
    }

    @Override
    @Transactional
    public CheckoutVerifyResponse verifyCheckout(String email, CheckoutVerifyRequest request) {
        Order order = orderRepository.findByOrderNumberWithItems(request.getOrderNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", request.getOrderNumber()));

        List<Payment> payments = paymentRepository.findByOrderId(order.getId());
        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("Payment", "orderNumber", request.getOrderNumber());
        }
        Payment payment = payments.get(0);

        PaymentVerifyResult verifyResult = paymentService.verifyOrderPayment(
                order,
                payment.getId(),
                request.getGatewayPaymentId(),
                request.getGatewaySignature(),
                request.getOtp()
        );

        if (verifyResult.isSuccess()) {
            if (order.getStatus() == OrderStatus.PLACED) {
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);

                for (OrderItem item : order.getItems()) {
                    inventoryService.deductStock(item.getVariant().getId(), null, item.getQuantity(), order.getOrderNumber());
                }

                cartService.clearCart(email, null);

                try {
                    Customer customer = order.getCustomer();
                    notificationService.handleOrderNotification(com.scentiva.modules.notification.event.OrderNotificationEvent.builder()
                            .customerId(customer.getId())
                            .customerEmail(email)
                            .customerName(customer.getFirstName())
                            .orderNumber(order.getOrderNumber())
                            .eventType("CONFIRMED")
                            .totalAmount(order.getTotalAmount())
                            .build());
                } catch (Exception e) {
                    log.warn("Failed to dispatch order confirmation notification for order={}: {}", order.getOrderNumber(), e.getMessage());
                }
            }

            return CheckoutVerifyResponse.builder()
                    .orderNumber(order.getOrderNumber())
                    .orderStatus(order.getStatus())
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .success(true)
                    .message("Payment verified and order confirmed!")
                    .build();
        } else {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            for (OrderItem item : order.getItems()) {
                inventoryService.releaseStockReservation(item.getVariant().getId(), null, item.getQuantity(), order.getOrderNumber());
            }

            return CheckoutVerifyResponse.builder()
                    .orderNumber(order.getOrderNumber())
                    .orderStatus(OrderStatus.CANCELLED)
                    .paymentStatus(PaymentStatus.FAILED)
                    .success(false)
                    .message(verifyResult.getMessage())
                    .build();
        }
    }

    private Customer getCustomerByEmail(String email) {
        return customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));
    }

    private AddressResponse mapToAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .customerId(address.getCustomer().getId())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.isDefault())
                .addressType(address.getAddressType())
                .createdAt(address.getCreatedAt())
                .build();
    }
}
