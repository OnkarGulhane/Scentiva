package com.scentiva.modules.returns.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.inventory.dto.InventoryAdjustRequest;
import com.scentiva.modules.inventory.model.InventoryRecord;
import com.scentiva.modules.inventory.model.MovementReason;
import com.scentiva.modules.inventory.repository.InventoryRecordRepository;
import com.scentiva.modules.inventory.service.InventoryService;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.model.OrderItem;
import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.order.repository.OrderItemRepository;
import com.scentiva.modules.order.repository.OrderRepository;
import com.scentiva.modules.payment.model.Payment;
import com.scentiva.modules.payment.model.PaymentStatus;
import com.scentiva.modules.payment.repository.PaymentRepository;
import com.scentiva.modules.payment.service.PaymentService;
import com.scentiva.modules.returns.dto.*;
import com.scentiva.modules.returns.model.ReturnItem;
import com.scentiva.modules.returns.model.ReturnRequest;
import com.scentiva.modules.returns.model.ReturnStatus;
import com.scentiva.modules.returns.repository.ReturnItemRepository;
import com.scentiva.modules.returns.repository.ReturnRepository;
import com.scentiva.modules.returns.service.ReturnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRepository returnRepository;
    private final ReturnItemRepository returnItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final InventoryService inventoryService;
    private final InventoryRecordRepository inventoryRecordRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public ReturnResponse createReturn(String email, ReturnCreateRequest request) {
        Customer customer = customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));

        Order order = orderRepository.findByOrderNumberWithItems(request.getOrderNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", request.getOrderNumber()));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Unauthorized return request for order: " + request.getOrderNumber());
        }

        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.SHIPPED && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed, shipped, or delivered orders can be requested for return. Current status: " + order.getStatus());
        }

        String returnNumber = "RET-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        BigDecimal refundTotal = BigDecimal.ZERO;
        List<ReturnItem> returnItems = new ArrayList<>();

        ReturnRequest returnRequest = ReturnRequest.builder()
                .order(order)
                .customer(customer)
                .returnNumber(returnNumber)
                .status(ReturnStatus.REQUESTED)
                .reason(request.getReason())
                .refundAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        returnRequest = returnRepository.save(returnRequest);

        for (ReturnItemRequest itemReq : request.getItems()) {
            OrderItem orderItem = orderItemRepository.findById(itemReq.getOrderItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", itemReq.getOrderItemId()));

            if (!orderItem.getOrder().getId().equals(order.getId())) {
                throw new IllegalArgumentException("Order item does not belong to order: " + order.getOrderNumber());
            }

            if (itemReq.getQuantity() > orderItem.getQuantity()) {
                throw new IllegalArgumentException("Return quantity cannot exceed purchased quantity");
            }

            BigDecimal itemRefund = orderItem.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            refundTotal = refundTotal.add(itemRefund);

            ReturnItem returnItem = ReturnItem.builder()
                    .returnRequest(returnRequest)
                    .orderItem(orderItem)
                    .quantity(itemReq.getQuantity())
                    .reason(itemReq.getReason() != null ? itemReq.getReason() : request.getReason())
                    .build();

            returnRequest.addItem(returnItem);
            returnItemRepository.save(returnItem);
        }

        returnRequest.setRefundAmount(refundTotal);
        returnRequest = returnRepository.save(returnRequest);

        log.info("Created return request id={}, number={} for order={}, refundAmount={}",
                returnRequest.getId(), returnRequest.getReturnNumber(), order.getOrderNumber(), returnRequest.getRefundAmount());

        return mapToReturnResponse(returnRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnResponse getReturnByNumber(String email, String returnNumber) {
        ReturnRequest returnRequest = returnRepository.findByReturnNumberWithItems(returnNumber)
                .orElseThrow(() -> new ResourceNotFoundException("ReturnRequest", "returnNumber", returnNumber));

        if (email != null && !email.trim().isEmpty()) {
            Customer customer = customerRepository.findByUserEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));
            if (!returnRequest.getCustomer().getId().equals(customer.getId())) {
                throw new IllegalArgumentException("Unauthorized access to return: " + returnNumber);
            }
        }

        return mapToReturnResponse(returnRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiPaginatedResponse<ReturnResponse> getCustomerReturns(String email, Pageable pageable) {
        Customer customer = customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));

        Page<ReturnRequest> page = returnRepository.findByCustomerIdAndIsDeletedFalse(customer.getId(), pageable);

        List<ReturnResponse> items = page.getContent().stream()
                .map(this::mapToReturnResponse)
                .toList();

        return ApiPaginatedResponse.of(items, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Override
    @Transactional
    public ReturnResponse updateReturnStatus(String returnNumber, ReturnStatusUpdateRequest request) {
        ReturnRequest returnRequest = returnRepository.findByReturnNumberWithItems(returnNumber)
                .orElseThrow(() -> new ResourceNotFoundException("ReturnRequest", "returnNumber", returnNumber));

        returnRequest.setStatus(request.getStatus());
        if (request.getPickupTrackingNumber() != null) {
            returnRequest.setPickupTrackingNumber(request.getPickupTrackingNumber());
        }
        if (request.getAdminNotes() != null) {
            returnRequest.setAdminNotes(request.getAdminNotes());
        }

        // If marked REFUNDED: restock items into warehouse and process refund
        if (request.getStatus() == ReturnStatus.REFUNDED) {
            Order order = returnRequest.getOrder();

            for (ReturnItem item : returnRequest.getItems()) {
                OrderItem orderItem = item.getOrderItem();
                List<InventoryRecord> records = inventoryRecordRepository.findByVariantIdAndIsDeletedFalse(orderItem.getVariant().getId());
                Long targetWarehouseId = !records.isEmpty() ? records.get(0).getWarehouse().getId() : 1L;

                inventoryService.adjustStock(InventoryAdjustRequest.builder()
                        .variantId(orderItem.getVariant().getId())
                        .warehouseId(targetWarehouseId)
                        .changeQuantity(item.getQuantity())
                        .reason(MovementReason.RETURN)
                        .referenceId("RETURN-" + returnNumber)
                        .build());
            }

            List<Payment> payments = paymentRepository.findByOrderId(order.getId());
            if (!payments.isEmpty()) {
                Payment payment = payments.get(0);
                if (payment.getStatus() == PaymentStatus.SUCCESS) {
                    paymentService.processRefund(order, payment.getId(), "Return approved: " + returnRequest.getReason());
                }
            }

            order.setStatus(OrderStatus.REFUNDED);
            orderRepository.save(order);
            log.info("Processed return refund and marked order={} as REFUNDED", order.getOrderNumber());
        }

        returnRequest = returnRepository.save(returnRequest);
        return mapToReturnResponse(returnRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiPaginatedResponse<ReturnResponse> getAllReturns(ReturnStatus status, Pageable pageable) {
        Page<ReturnRequest> page = (status != null)
                ? returnRepository.findByStatusAndIsDeletedFalse(status, pageable)
                : returnRepository.findAll(pageable);

        List<ReturnResponse> items = page.getContent().stream()
                .map(this::mapToReturnResponse)
                .toList();

        return ApiPaginatedResponse.of(items, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    private ReturnResponse mapToReturnResponse(ReturnRequest returnRequest) {
        List<ReturnItemResponse> itemResponses = returnRequest.getItems() != null
                ? returnRequest.getItems().stream().map(this::mapToReturnItemResponse).toList()
                : List.of();

        return ReturnResponse.builder()
                .id(returnRequest.getId())
                .returnNumber(returnRequest.getReturnNumber())
                .orderId(returnRequest.getOrder().getId())
                .orderNumber(returnRequest.getOrder().getOrderNumber())
                .customerId(returnRequest.getCustomer().getId())
                .customerName(returnRequest.getCustomer().getFullName())
                .status(returnRequest.getStatus())
                .reason(returnRequest.getReason())
                .refundAmount(returnRequest.getRefundAmount())
                .pickupTrackingNumber(returnRequest.getPickupTrackingNumber())
                .adminNotes(returnRequest.getAdminNotes())
                .items(itemResponses)
                .createdAt(returnRequest.getCreatedAt())
                .updatedAt(returnRequest.getUpdatedAt())
                .build();
    }

    private ReturnItemResponse mapToReturnItemResponse(ReturnItem item) {
        return ReturnItemResponse.builder()
                .id(item.getId())
                .orderItemId(item.getOrderItem().getId())
                .variantId(item.getOrderItem().getVariant().getId())
                .sku(item.getOrderItem().getSku())
                .productName(item.getOrderItem().getProductName())
                .variantTitle(item.getOrderItem().getVariantTitle())
                .quantity(item.getQuantity())
                .unitPrice(item.getOrderItem().getUnitPrice())
                .reason(item.getReason())
                .build();
    }
}
