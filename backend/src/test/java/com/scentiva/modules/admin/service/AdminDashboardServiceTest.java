package com.scentiva.modules.admin.service;

import com.scentiva.modules.admin.dto.DashboardSummaryResponse;
import com.scentiva.modules.admin.dto.OrderStatusCountDto;
import com.scentiva.modules.admin.dto.SalesAnalyticsResponse;
import com.scentiva.modules.admin.dto.TopProductPerformanceDto;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminDashboardServiceTest {

    @Autowired
    private AdminDashboardService adminDashboardService;

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
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private static final String TEST_EMAIL = "admin.dash@scentiva.luxury";

    @BeforeEach
    void setUp() {
        User user = userRepository.findByEmailAndIsDeletedFalse(TEST_EMAIL)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(TEST_EMAIL)
                        .passwordHash("hashed")
                        .role(Role.ROLE_ADMIN)
                        .build()));

        Customer customer = customerRepository.findByUserEmail(TEST_EMAIL)
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("Admin")
                        .lastName("Dash")
                        .build()));

        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("roja-dash")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Roja Parfums")
                        .slug("roja-dash")
                        .originCountry("United Kingdom")
                        .tier(BrandTier.HERITAGE_MAISON)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("amber-dash")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Amber")
                        .slug("amber-dash")
                        .build()));

        Product product = productRepository.findBySlugAndIsDeletedFalse("elysium-dash")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Elysium Parfum")
                        .slug("elysium-dash")
                        .gender(GenderTarget.UNISEX)
                        .isActive(true)
                        .build()));

        ProductVariant variant = productVariantRepository.findBySkuAndIsDeletedFalse("ROJ-ELY-100-DASH")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("ROJ-ELY-100-DASH")
                        .volumeMl(100)
                        .concentration(Concentration.PARFUM)
                        .basePrice(new BigDecimal("32000.00"))
                        .isActive(true)
                        .build()));

        Order order = orderRepository.save(Order.builder()
                .customer(customer)
                .orderNumber("ORD-DASH-" + System.currentTimeMillis())
                .status(OrderStatus.DELIVERED)
                .subtotal(new BigDecimal("32000.00"))
                .discountAmount(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .taxAmount(new BigDecimal("5760.00"))
                .totalAmount(new BigDecimal("37760.00"))
                .items(new ArrayList<>())
                .build());

        OrderItem orderItem = orderItemRepository.save(OrderItem.builder()
                .order(order)
                .variant(variant)
                .sku(variant.getSku())
                .productName("Elysium Parfum")
                .variantTitle("100ml Parfum")
                .unitPrice(new BigDecimal("32000.00"))
                .quantity(2)
                .totalPrice(new BigDecimal("64000.00"))
                .build());
        order.addItem(orderItem);
    }

    @Test
    @DisplayName("Should retrieve dashboard summary KPIs accurately")
    void shouldGetDashboardSummary() {
        DashboardSummaryResponse summary = adminDashboardService.getDashboardSummary();

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalOrders()).isGreaterThanOrEqualTo(1);
        assertThat(summary.getTotalRevenue()).isGreaterThan(BigDecimal.ZERO);
        assertThat(summary.getAverageOrderValue()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should aggregate sales analytics and trends")
    void shouldGetSalesAnalytics() {
        SalesAnalyticsResponse response = adminDashboardService.getSalesAnalytics();

        assertThat(response).isNotNull();
        assertThat(response.getTotalRevenue()).isGreaterThan(BigDecimal.ZERO);
        assertThat(response.getTrends()).isNotEmpty();
    }

    @Test
    @DisplayName("Should rank top performing products")
    void shouldGetTopProducts() {
        List<TopProductPerformanceDto> topProducts = adminDashboardService.getTopSellingProducts(5);

        assertThat(topProducts).isNotEmpty();
        assertThat(topProducts.get(0).getProductName()).isEqualTo("Elysium Parfum");
        assertThat(topProducts.get(0).getTotalQuantitySold()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should provide order status breakdown")
    void shouldGetOrderStatusBreakdown() {
        List<OrderStatusCountDto> breakdown = adminDashboardService.getOrderStatusBreakdown();

        assertThat(breakdown).isNotEmpty();
        assertThat(breakdown.stream().anyMatch(dto -> dto.getStatus() == OrderStatus.DELIVERED && dto.getCount() >= 1)).isTrue();
    }
}
