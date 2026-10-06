package com.scentiva.modules.order.service;

import com.scentiva.common.exception.ResourceNotFoundException;
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
import com.scentiva.modules.order.dto.InvoiceResponse;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.model.OrderItem;
import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.order.repository.OrderItemRepository;
import com.scentiva.modules.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InvoiceServiceTest {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

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

    private User customerUser;
    private User otherCustomerUser;
    private User adminUser;
    private Customer customer;
    private Customer otherCustomer;
    private ProductVariant variant;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        customerUser = userRepository.save(User.builder()
                .email("invoice.customer." + System.currentTimeMillis() + "@scentiva.test")
                .passwordHash("$2a$12$eX4mP1eHashForTestingOnlySecure1234567890")
                .role(Role.ROLE_CUSTOMER)
                .build());

        otherCustomerUser = userRepository.save(User.builder()
                .email("other.customer." + System.currentTimeMillis() + "@scentiva.test")
                .passwordHash("$2a$12$eX4mP1eHashForTestingOnlySecure1234567890")
                .role(Role.ROLE_CUSTOMER)
                .build());

        adminUser = userRepository.save(User.builder()
                .email("admin.invoice." + System.currentTimeMillis() + "@scentiva.test")
                .passwordHash("$2a$12$eX4mP1eHashForTestingOnlySecure1234567890")
                .role(Role.ROLE_ADMIN)
                .build());

        customer = customerRepository.save(Customer.builder()
                .user(customerUser)
                .firstName("Elena")
                .lastName("Rostova")
                .phone("+919876543210")
                .build());

        otherCustomer = customerRepository.save(Customer.builder()
                .user(otherCustomerUser)
                .firstName("Dmitri")
                .lastName("Karpov")
                .phone("+919876543211")
                .build());

        String ts = String.valueOf(System.currentTimeMillis());
        Brand brand = brandRepository.save(Brand.builder()
                .name("Maison Francis Kurkdjian " + ts)
                .slug("mfk-inv-" + ts)
                .originCountry("France")
                .tier(BrandTier.HERITAGE_MAISON)
                .build());

        Category category = categoryRepository.save(Category.builder()
                .name("Extrait de Parfum " + ts)
                .slug("extrait-inv-" + ts)
                .build());

        Product product = productRepository.save(Product.builder()
                .brand(brand)
                .category(category)
                .name("Baccarat Rouge 540 Extrait " + ts)
                .slug("baccarat-540-inv-" + ts)
                .gender(GenderTarget.UNISEX)
                .isActive(true)
                .build());

        variant = productVariantRepository.save(ProductVariant.builder()
                .product(product)
                .sku("MFK-BR540-INV-" + System.currentTimeMillis())
                .volumeMl(70)
                .concentration(Concentration.EXTRAIT)
                .basePrice(new BigDecimal("38500.00"))
                .salePrice(new BigDecimal("38500.00"))
                .isActive(true)
                .build());

        testOrder = Order.builder()
                .customer(customer)
                .orderNumber("ORD-INV-SVC-" + System.currentTimeMillis())
                .status(OrderStatus.CONFIRMED)
                .subtotal(new BigDecimal("38500.00"))
                .discountAmount(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .taxAmount(new BigDecimal("6930.00"))
                .totalAmount(new BigDecimal("45430.00"))
                .items(new ArrayList<>())
                .build();

        testOrder = orderRepository.save(testOrder);

        OrderItem item = OrderItem.builder()
                .order(testOrder)
                .variant(variant)
                .sku(variant.getSku())
                .productName(product.getName())
                .variantTitle("70ml Extrait")
                .unitPrice(variant.getBasePrice())
                .quantity(1)
                .totalPrice(variant.getBasePrice())
                .build();

        testOrder.getItems().add(orderItemRepository.save(item));
    }

    @Test
    @DisplayName("Customer gets invoice with idempotent INV-YYYY-XXXXXX number")
    void testGetOrderInvoice_CustomerSuccess() {
        InvoiceResponse invoice = invoiceService.getOrderInvoice(customerUser.getEmail(), testOrder.getOrderNumber());

        assertThat(invoice).isNotNull();
        assertThat(invoice.getOrderNumber()).isEqualTo(testOrder.getOrderNumber());
        assertThat(invoice.getInvoiceNumber()).isNotNull();
        assertThat(invoice.getInvoiceNumber()).startsWith("INV-");
        assertThat(invoice.getGrandTotal()).isEqualByComparingTo(new BigDecimal("45430.00"));
        assertThat(invoice.getCustomerName()).isEqualTo("Elena Rostova");
        assertThat(invoice.getItems()).hasSize(1);
        assertThat(invoice.getItems().get(0).getProductName()).startsWith("Baccarat Rouge 540 Extrait");

        // Subsequent call returns exact same invoice number
        InvoiceResponse secondCall = invoiceService.getOrderInvoice(customerUser.getEmail(), testOrder.getOrderNumber());
        assertThat(secondCall.getInvoiceNumber()).isEqualTo(invoice.getInvoiceNumber());
    }

    @Test
    @DisplayName("Admin can view any customer's order invoice")
    void testGetOrderInvoice_AdminSuccess() {
        InvoiceResponse invoice = invoiceService.getOrderInvoice(adminUser.getEmail(), testOrder.getOrderNumber());

        assertThat(invoice).isNotNull();
        assertThat(invoice.getOrderNumber()).isEqualTo(testOrder.getOrderNumber());
        assertThat(invoice.getCustomerEmail()).isEqualTo(customerUser.getEmail());
    }

    @Test
    @DisplayName("Unauthorized customer receives AccessDeniedException")
    void testGetOrderInvoice_UnauthorizedAccessDenied() {
        assertThatThrownBy(() -> invoiceService.getOrderInvoice(otherCustomerUser.getEmail(), testOrder.getOrderNumber()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("Non-existent order throws ResourceNotFoundException")
    void testGetOrderInvoice_NotFound() {
        assertThatThrownBy(() -> invoiceService.getOrderInvoice(customerUser.getEmail(), "ORD-NON-EXISTENT-999"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Generate binary PDF byte array with valid PDF magic bytes")
    void testGetOrderInvoicePdf_Success() {
        byte[] pdfBytes = invoiceService.getOrderInvoicePdf(customerUser.getEmail(), testOrder.getOrderNumber());

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(100);
        // PDF files start with "%PDF-" (0x25 0x50 0x44 0x46 0x2D)
        String header = new String(pdfBytes, 0, 5);
        assertThat(header).isEqualTo("%PDF-");
    }
}
