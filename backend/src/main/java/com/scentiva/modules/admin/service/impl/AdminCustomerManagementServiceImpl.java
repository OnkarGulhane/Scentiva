package com.scentiva.modules.admin.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.admin.dto.AdminCustomerDetailResponse;
import com.scentiva.modules.admin.dto.AdminCustomerSummaryResponse;
import com.scentiva.modules.admin.service.AdminCustomerManagementService;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.model.UserStatus;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.customer.dto.AddressResponse;
import com.scentiva.modules.customer.model.Address;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.AddressRepository;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.order.dto.OrderSummaryResponse;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCustomerManagementServiceImpl implements AdminCustomerManagementService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiPaginatedResponse<AdminCustomerSummaryResponse> getAllCustomers(Pageable pageable) {
        Page<Customer> page = customerRepository.findAll(pageable);

        List<AdminCustomerSummaryResponse> items = page.getContent().stream()
                .map(this::mapToSummary)
                .toList();

        return ApiPaginatedResponse.of(items, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminCustomerDetailResponse getCustomerDetails(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        List<Address> addresses = addressRepository.findByCustomerIdAndIsDeletedFalse(customer.getId());
        List<Order> orders = orderRepository.findByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(customer.getId());

        long totalOrders = orders.size();
        BigDecimal totalSpent = orderRepository.sumCustomerSpend(customer.getId());

        List<AddressResponse> addressResponses = addresses.stream()
                .map(this::mapToAddressResponse)
                .toList();

        List<OrderSummaryResponse> orderResponses = orders.stream()
                .map(this::mapToOrderSummaryResponse)
                .toList();

        return AdminCustomerDetailResponse.builder()
                .customerId(customer.getId())
                .userId(customer.getUser().getId())
                .email(customer.getUser().getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phone(customer.getPhone())
                .loyaltyTier(customer.getLoyaltyTier())
                .totalOrders(totalOrders)
                .totalSpent(totalSpent)
                .isUserActive(customer.getUser().getStatus() == UserStatus.ACTIVE)
                .addresses(addressResponses)
                .recentOrders(orderResponses)
                .registeredAt(customer.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void updateCustomerStatus(Long customerId, UserStatus status) {
        Customer customer = customerRepository.findById(customerId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        User user = customer.getUser();
        user.setStatus(status);
        userRepository.save(user);

        log.info("Updated customer id={}, email={} status to {}", customerId, user.getEmail(), status);
    }

    private AdminCustomerSummaryResponse mapToSummary(Customer customer) {
        long orderCount = orderRepository.countByCustomerIdAndIsDeletedFalse(customer.getId());
        BigDecimal totalSpent = orderRepository.sumCustomerSpend(customer.getId());

        return AdminCustomerSummaryResponse.builder()
                .customerId(customer.getId())
                .userId(customer.getUser().getId())
                .email(customer.getUser().getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phone(customer.getPhone())
                .loyaltyTier(customer.getLoyaltyTier())
                .totalOrders(orderCount)
                .totalSpent(totalSpent)
                .isUserActive(customer.getUser().getStatus() == UserStatus.ACTIVE)
                .registeredAt(customer.getCreatedAt())
                .build();
    }

    private AddressResponse mapToAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .phone(address.getPhone())
                .addressType(address.getAddressType())
                .isDefault(address.isDefault())
                .build();
    }

    private OrderSummaryResponse mapToOrderSummaryResponse(Order order) {
        int itemCount = order.getItems() != null
                ? order.getItems().stream().mapToInt(com.scentiva.modules.order.model.OrderItem::getQuantity).sum()
                : 0;

        return OrderSummaryResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .totalItems(itemCount)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
