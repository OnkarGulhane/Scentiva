package com.scentiva.modules.order.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.inventory.dto.InventoryAdjustRequest;
import com.scentiva.modules.inventory.model.MovementReason;
import com.scentiva.modules.inventory.service.InventoryService;
import com.scentiva.modules.order.dto.*;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.model.OrderItem;
import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.order.repository.OrderRepository;
import com.scentiva.modules.order.service.OrderService;
import com.scentiva.modules.payment.model.Payment;
import com.scentiva.modules.payment.model.PaymentStatus;
import com.scentiva.modules.payment.repository.PaymentRepository;
import com.scentiva.modules.payment.service.PaymentService;
import com.scentiva.modules.shipping.dto.ShipmentEventResponse;
import com.scentiva.modules.shipping.dto.ShipmentResponse;
import com.scentiva.modules.shipping.dto.ShipmentTrackingResponse;
import com.scentiva.modules.shipping.model.Shipment;
import com.scentiva.modules.shipping.model.ShipmentEvent;
import com.scentiva.modules.shipping.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.scentiva.modules.inventory.model.InventoryRecord;
import com.scentiva.modules.inventory.repository.InventoryRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final InventoryService inventoryService;
    private final InventoryRecordRepository inventoryRecordRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final ShipmentRepository shipmentRepository;
    private final com.scentiva.modules.notification.service.NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public ApiPaginatedResponse<OrderSummaryResponse> getCustomerOrders(String email, Pageable pageable) {
        Customer customer = getCustomerByEmail(email);
        Page<Order> orderPage = orderRepository.findByCustomerIdAndIsDeletedFalse(customer.getId(), pageable);

        List<OrderSummaryResponse> content = orderPage.getContent().stream()
                .map(this::mapToOrderSummary)
                .toList();

        return ApiPaginatedResponse.of(
                content,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String email, String orderNumber) {
        Order order = orderRepository.findByOrderNumberWithItems(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));

        if (email != null && !email.trim().isEmpty()) {
            Customer customer = getCustomerByEmail(email);
            if (!order.getCustomer().getId().equals(customer.getId())) {
                throw new IllegalArgumentException("Unauthorized access to order: " + orderNumber);
            }
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String email, String orderNumber, OrderCancelRequest request) {
        Order order = orderRepository.findByOrderNumberWithItems(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));

        if (email != null && !email.trim().isEmpty()) {
            Customer customer = getCustomerByEmail(email);
            if (!order.getCustomer().getId().equals(customer.getId())) {
                throw new IllegalArgumentException("Unauthorized access to order: " + orderNumber);
            }
        }

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Orders that are already shipped or delivered cannot be cancelled directly. Please initiate a return request.");
        }

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.REFUNDED) {
            throw new IllegalStateException("Order is already " + order.getStatus());
        }

        OrderStatus originalStatus = order.getStatus();

        // 1. Release or Restock Inventory
        if (originalStatus == OrderStatus.PLACED) {
            for (OrderItem item : order.getItems()) {
                inventoryService.releaseStockReservation(item.getVariant().getId(), null, item.getQuantity(), order.getOrderNumber());
            }
        } else if (originalStatus == OrderStatus.CONFIRMED || originalStatus == OrderStatus.PROCESSING) {
            for (OrderItem item : order.getItems()) {
                List<InventoryRecord> records = inventoryRecordRepository.findByVariantIdAndIsDeletedFalse(item.getVariant().getId());
                Long targetWarehouseId = !records.isEmpty() ? records.get(0).getWarehouse().getId() : 1L;
                inventoryService.adjustStock(InventoryAdjustRequest.builder()
                        .variantId(item.getVariant().getId())
                        .warehouseId(targetWarehouseId)
                        .changeQuantity(item.getQuantity())
                        .reason(MovementReason.RETURN)
                        .referenceId("CANCEL-" + order.getOrderNumber())
                        .build());
            }
        }

        // 2. Process Refund if payment was captured
        List<Payment> payments = paymentRepository.findByOrderId(order.getId());
        if (!payments.isEmpty()) {
            Payment payment = payments.get(0);
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                paymentService.processRefund(order, payment.getId(), request.getReason());
            }
        }

        // 3. Mark Order as CANCELLED
        order.setStatus(OrderStatus.CANCELLED);
        String reasonNote = "[Cancelled by user: " + request.getReason() + "]";
        order.setNotes(order.getNotes() != null ? order.getNotes() + " | " + reasonNote : reasonNote);
        order = orderRepository.save(order);

        log.info("Cancelled order={}, previousStatus={}, reason={}", order.getOrderNumber(), originalStatus, request.getReason());

        try {
            Customer customer = order.getCustomer();
            notificationService.handleOrderNotification(com.scentiva.modules.notification.event.OrderNotificationEvent.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getUser() != null ? customer.getUser().getEmail() : null)
                    .customerName(customer.getFirstName())
                    .orderNumber(order.getOrderNumber())
                    .eventType("CANCELLED")
                    .totalAmount(order.getTotalAmount())
                    .build());
        } catch (Exception e) {
            log.warn("Failed to dispatch cancel notification for order={}: {}", order.getOrderNumber(), e.getMessage());
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(String orderNumber, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findByOrderNumberWithItems(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));

        order.setStatus(request.getStatus());
        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            order.setNotes(order.getNotes() != null ? order.getNotes() + " | " + request.getNotes() : request.getNotes());
        }

        order = orderRepository.save(order);
        log.info("Updated order={} status to {}", order.getOrderNumber(), request.getStatus());

        try {
            Customer customer = order.getCustomer();
            notificationService.handleOrderNotification(com.scentiva.modules.notification.event.OrderNotificationEvent.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getUser() != null ? customer.getUser().getEmail() : null)
                    .customerName(customer.getFirstName())
                    .orderNumber(order.getOrderNumber())
                    .eventType(request.getStatus().name())
                    .totalAmount(order.getTotalAmount())
                    .build());
        } catch (Exception e) {
            log.warn("Failed to dispatch status update notification for order={}: {}", order.getOrderNumber(), e.getMessage());
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiPaginatedResponse<OrderSummaryResponse> getAllOrders(OrderStatus status, Pageable pageable) {
        Page<Order> orderPage = (status != null)
                ? orderRepository.findByStatusAndIsDeletedFalse(status, pageable)
                : orderRepository.findByIsDeletedFalse(pageable);

        List<OrderSummaryResponse> content = orderPage.getContent().stream()
                .map(this::mapToOrderSummary)
                .toList();

        return ApiPaginatedResponse.of(
                content,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements()
        );
    }

    @Override
    @Transactional
    public void deleteOrder(String orderNumber) {
        Order order = orderRepository.findByOrderNumberAndIsDeletedFalse(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        order.setDeleted(true);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Order={} marked as deleted/cancelled by admin", orderNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentTrackingResponse trackOrder(String email, String orderNumber) {
        Order order = orderRepository.findByOrderNumberAndIsDeletedFalse(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));

        if (email != null && !email.trim().isEmpty()) {
            Customer customer = getCustomerByEmail(email);
            if (!order.getCustomer().getId().equals(customer.getId())) {
                throw new IllegalArgumentException("Unauthorized access to order: " + orderNumber);
            }
        }

        Optional<Shipment> shipmentOpt = shipmentRepository.findByOrderIdWithEvents(order.getId());
        if (shipmentOpt.isEmpty()) {
            return ShipmentTrackingResponse.builder()
                    .orderNumber(order.getOrderNumber())
                    .carrierName("Scentiva Express Delivery")
                    .currentStatus(null)
                    .timeline(List.of())
                    .build();
        }

        Shipment shipment = shipmentOpt.get();
        List<ShipmentEventResponse> timeline = shipment.getEvents().stream()
                .map(this::mapToEventResponse)
                .toList();

        return ShipmentTrackingResponse.builder()
                .orderNumber(order.getOrderNumber())
                .trackingNumber(shipment.getTrackingNumber())
                .carrierName(shipment.getCarrierName())
                .currentStatus(shipment.getStatus())
                .estimatedDelivery(shipment.getDispatchedAt() != null ? shipment.getDispatchedAt().plusDays(3) : null)
                .timeline(timeline)
                .build();
    }

    private Customer getCustomerByEmail(String email) {
        return customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));
    }

    private OrderSummaryResponse mapToOrderSummary(Order order) {
        int totalItems = order.getItems() != null
                ? order.getItems().stream().mapToInt(OrderItem::getQuantity).sum()
                : 0;

        Optional<Shipment> shipmentOpt = shipmentRepository.findByOrderIdAndIsDeletedFalse(order.getId());

        return OrderSummaryResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalItems(totalItems)
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .trackingNumber(shipmentOpt.map(Shipment::getTrackingNumber).orElse(null))
                .carrierName(shipmentOpt.map(Shipment::getCarrierName).orElse(null))
                .build();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems() != null
                ? order.getItems().stream().map(this::mapToOrderItemResponse).toList()
                : List.of();

        List<Payment> payments = paymentRepository.findByOrderId(order.getId());
        Payment latestPayment = payments.isEmpty() ? null : payments.get(payments.size() - 1);

        Optional<Shipment> shipmentOpt = shipmentRepository.findByOrderIdWithEvents(order.getId());
        ShipmentResponse shipmentResponse = shipmentOpt.map(this::mapToShipmentResponse).orElse(null);

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomer().getId())
                .customerEmail(order.getCustomer().getUser().getEmail())
                .customerName(order.getCustomer().getFullName())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .deliveryFee(order.getDeliveryFee())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .notes(order.getNotes())
                .items(itemResponses)
                .customerSnapshotJson(order.getSnapshot() != null ? order.getSnapshot().getCustomerSnapshotJson() : null)
                .shippingAddressSnapshotJson(order.getSnapshot() != null ? order.getSnapshot().getShippingAddressSnapshotJson() : null)
                .pricingMatrixSnapshotJson(order.getSnapshot() != null ? order.getSnapshot().getPricingMatrixSnapshotJson() : null)
                .paymentStatus(latestPayment != null ? latestPayment.getStatus() : null)
                .paymentMethod(latestPayment != null ? latestPayment.getPaymentMethod() : null)
                .shipment(shipmentResponse)
                .invoiceNumber(order.getInvoiceNumber())
                .invoiceGeneratedAt(order.getInvoiceGeneratedAt())
                .invoiceStatus(order.getInvoiceStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .variantId(item.getVariant().getId())
                .sku(item.getSku())
                .productName(item.getProductName())
                .variantTitle(item.getVariantTitle())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    private ShipmentResponse mapToShipmentResponse(Shipment shipment) {
        List<ShipmentEventResponse> events = shipment.getEvents() != null
                ? shipment.getEvents().stream().map(this::mapToEventResponse).toList()
                : List.of();

        return ShipmentResponse.builder()
                .id(shipment.getId())
                .orderId(shipment.getOrder().getId())
                .orderNumber(shipment.getOrder().getOrderNumber())
                .provider(shipment.getProvider())
                .carrierName(shipment.getCarrierName())
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus())
                .dispatchedAt(shipment.getDispatchedAt())
                .deliveredAt(shipment.getDeliveredAt())
                .events(events)
                .createdAt(shipment.getCreatedAt())
                .build();
    }

    private ShipmentEventResponse mapToEventResponse(ShipmentEvent event) {
        return ShipmentEventResponse.builder()
                .id(event.getId())
                .status(event.getStatus())
                .location(event.getLocation())
                .description(event.getDescription())
                .eventTimestamp(event.getEventTimestamp())
                .build();
    }
}
