package com.scentiva.modules.order.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.customer.model.Address;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.order.dto.InvoiceItemResponse;
import com.scentiva.modules.order.dto.InvoiceResponse;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.model.OrderItem;
import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.order.repository.OrderRepository;
import com.scentiva.modules.order.service.InvoiceService;
import com.scentiva.modules.order.service.PdfInvoiceGenerator;
import com.scentiva.modules.payment.model.Payment;
import com.scentiva.modules.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PdfInvoiceGenerator pdfInvoiceGenerator;

    @Value("${scentiva.invoice.company-name:SCENTIVA Haute Parfumerie Private Limited}")
    private String companyName;

    @Value("${scentiva.invoice.brand-tagline:Haute Parfumerie & Luxury Fragrance Atelier}")
    private String brandTagline;

    @Value("${scentiva.invoice.registered-address:Aura Prestige Towers, Level 4, Baner High Street, Pune, Maharashtra 411045, India}")
    private String registeredAddress;

    @Value("${scentiva.invoice.support-email:concierge@scentiva.com}")
    private String supportEmail;

    @Value("${scentiva.invoice.support-phone:+91 20 4911 2026}")
    private String supportPhone;

    @Value("${scentiva.invoice.website:https://scentiva.luxury}")
    private String website;

    @Value("${scentiva.invoice.tax-id:27AALCS9812K1Z0}")
    private String taxId;

    @Value("${scentiva.invoice.currency:INR}")
    private String currency;

    @Override
    @Transactional
    public InvoiceResponse getOrderInvoice(String userEmail, String orderNumber) {
        Order order = orderRepository.findByOrderNumberWithItems(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));

        validateOwnership(userEmail, order);

        // Ensure invoice number is generated idempotently
        if (order.getInvoiceNumber() == null || order.getInvoiceNumber().isBlank()) {
            return generateInvoiceForOrder(order);
        }

        return buildInvoiceResponse(order);
    }

    @Override
    @Transactional
    public byte[] getOrderInvoicePdf(String userEmail, String orderNumber) {
        InvoiceResponse invoice = getOrderInvoice(userEmail, orderNumber);
        return pdfInvoiceGenerator.generateInvoicePdf(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse generateInvoiceForOrder(Order order) {
        if (order.getInvoiceNumber() == null || order.getInvoiceNumber().isBlank()) {
            int year = (order.getCreatedAt() != null) ? order.getCreatedAt().getYear() : LocalDateTime.now().getYear();
            String seq = String.format("%06d", (order.getId() != null ? order.getId() : (long) (Math.random() * 1000000)));
            String invoiceNumber = "INV-" + year + "-" + seq;

            order.setInvoiceNumber(invoiceNumber);
            order.setInvoiceGeneratedAt(LocalDateTime.now());
            order.setInvoiceStatus(order.getStatus() == OrderStatus.CANCELLED ? "CANCELLED" : "ISSUED");
            order = orderRepository.save(order);
            log.info("Generated persistent invoiceNumber={} for orderNumber={}", invoiceNumber, order.getOrderNumber());
        }

        return buildInvoiceResponse(order);
    }

    private void validateOwnership(String userEmail, Order order) {
        if (userEmail == null || userEmail.isBlank()) {
            return; // System or internal call
        }

        User user = userRepository.findByEmailAndIsDeletedFalse(userEmail).orElse(null);
        if (user != null && (user.getRole() == Role.ROLE_ADMIN || user.getRole() == Role.ROLE_SUPER_ADMIN)) {
            return; // Admin access authorized
        }

        Customer owner = order.getCustomer();
        if (owner == null || owner.getUser() == null || !owner.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            log.warn("Security Alert: User '{}' attempted unauthorized access to invoice for order '{}'", userEmail, order.getOrderNumber());
            throw new AccessDeniedException("You are not authorized to access this invoice.");
        }
    }

    private InvoiceResponse buildInvoiceResponse(Order order) {
        Customer customer = order.getCustomer();

        String customerName = (customer != null) ? customer.getFullName() : "Valued Customer";
        String customerEmail = (customer != null && customer.getUser() != null) ? customer.getUser().getEmail() : "";
        String customerPhone = (customer != null) ? customer.getPhone() : "";

        // Extract payment info
        List<Payment> payments = paymentRepository.findByOrderId(order.getId());
        Payment payment = payments.isEmpty() ? null : payments.get(0);

        String paymentProvider = (payment != null && payment.getProvider() != null) ? payment.getProvider().name() : "RAZORPAY";
        String paymentMethod = (payment != null && payment.getPaymentMethod() != null) ? payment.getPaymentMethod().name() : "ONLINE";
        String paymentStatus = (payment != null && payment.getStatus() != null) ? payment.getStatus().name() : (order.getStatus() == OrderStatus.CONFIRMED ? "SUCCESS" : "PENDING");
        String razorpayOrderId = (payment != null) ? payment.getGatewayOrderId() : null;

        // Line items
        List<InvoiceItemResponse> items = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                String brandName = (item.getVariant() != null && item.getVariant().getProduct() != null && item.getVariant().getProduct().getBrand() != null)
                        ? item.getVariant().getProduct().getBrand().getName() : "SCENTIVA";
                Integer volumeMl = (item.getVariant() != null) ? item.getVariant().getVolumeMl() : null;
                String concentration = (item.getVariant() != null && item.getVariant().getConcentration() != null)
                        ? item.getVariant().getConcentration().name() : null;

                items.add(InvoiceItemResponse.builder()
                        .id(item.getId())
                        .sku(item.getSku())
                        .productName(item.getProductName())
                        .brandName(brandName)
                        .volumeMl(volumeMl)
                        .concentration(concentration)
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build());
            }
        }

        // Addresses
        String shippingAddr = "Atelier Delivery Address";
        if (order.getSnapshot() != null && order.getSnapshot().getShippingAddressSnapshotJson() != null) {
            shippingAddr = order.getSnapshot().getShippingAddressSnapshotJson();
        }

        return InvoiceResponse.builder()
                .invoiceNumber(order.getInvoiceNumber())
                .orderNumber(order.getOrderNumber())
                .invoiceDate(order.getInvoiceGeneratedAt() != null ? order.getInvoiceGeneratedAt() : order.getCreatedAt())
                .orderDate(order.getCreatedAt())
                .invoiceStatus(order.getInvoiceStatus() != null ? order.getInvoiceStatus() : "ISSUED")
                .orderStatus(order.getStatus().name())
                .companyName(companyName)
                .brandTagline(brandTagline)
                .registeredAddress(registeredAddress)
                .supportEmail(supportEmail)
                .supportPhone(supportPhone)
                .website(website)
                .taxId(taxId)
                .customerName(customerName)
                .customerEmail(customerEmail)
                .customerPhone(customerPhone)
                .shippingAddress(shippingAddr)
                .billingAddress(shippingAddr)
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .deliveryFee(order.getDeliveryFee())
                .taxAmount(order.getTaxAmount())
                .grandTotal(order.getTotalAmount())
                .currency(currency)
                .paymentProvider(paymentProvider)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .razorpayOrderId(razorpayOrderId)
                .razorpayPaymentId(order.getNotes())
                .items(items)
                .build();
    }
}
