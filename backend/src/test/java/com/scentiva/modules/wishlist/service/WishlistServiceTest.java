package com.scentiva.modules.wishlist.service;

import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.ProductVariantRepository;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.inventory.service.InventoryService;
import com.scentiva.modules.wishlist.dto.WishlistResponse;
import com.scentiva.modules.wishlist.model.Wishlist;
import com.scentiva.modules.wishlist.model.WishlistItem;
import com.scentiva.modules.wishlist.repository.WishlistItemRepository;
import com.scentiva.modules.wishlist.repository.WishlistRepository;
import com.scentiva.modules.wishlist.service.impl.WishlistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    private User user;
    private Customer customer;
    private Product product;
    private ProductVariant variant;
    private Wishlist wishlist;

    @BeforeEach
    void setUp() {
        user = User.builder().email("aria.deshmukh@scentiva.luxury").role(Role.ROLE_CUSTOMER).build();
        user.setId(1L);

        customer = Customer.builder().user(user).firstName("Aria").lastName("Deshmukh").build();
        customer.setId(10L);

        Brand brand = Brand.builder().name("Byredo").slug("byredo").build();
        brand.setId(2L);

        Category category = Category.builder().name("Fresh").slug("fresh").build();
        category.setId(3L);

        product = Product.builder()
                .brand(brand)
                .category(category)
                .name("Gypsy Water")
                .slug("gypsy-water")
                .build();
        product.setId(20L);

        variant = ProductVariant.builder()
                .product(product)
                .sku("BY-GW-100")
                .volumeMl(100)
                .concentration(Concentration.EDP)
                .basePrice(new BigDecimal("2200.00"))
                .salePrice(new BigDecimal("2100.00"))
                .isActive(true)
                .build();
        variant.setId(100L);
        variant.setDeleted(false);

        wishlist = Wishlist.builder()
                .customer(customer)
                .items(new ArrayList<>())
                .build();
        wishlist.setId(50L);
        wishlist.setDeleted(false);
    }

    @Test
    @DisplayName("Should add fragrance variant to customer wishlist")
    void shouldAddToWishlist() {
        when(customerRepository.findByUserEmail("aria.deshmukh@scentiva.luxury")).thenReturn(Optional.of(customer));
        when(wishlistRepository.findByCustomerIdWithItems(10L)).thenReturn(Optional.of(wishlist));
        when(productVariantRepository.findById(100L)).thenReturn(Optional.of(variant));
        when(inventoryService.getTotalAvailableStock(100L)).thenReturn(15);
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(i -> i.getArgument(0));

        WishlistResponse response = wishlistService.addToWishlist("aria.deshmukh@scentiva.luxury", 100L);

        assertThat(response).isNotNull();
        assertThat(response.getTotalCount()).isEqualTo(1);
        assertThat(response.getItems().get(0).getSku()).isEqualTo("BY-GW-100");
        assertThat(response.getItems().get(0).isInStock()).isTrue();
    }

    @Test
    @DisplayName("Should remove variant from wishlist")
    void shouldRemoveFromWishlist() {
        WishlistItem item = WishlistItem.builder().wishlist(wishlist).variant(variant).build();
        wishlist.addItem(item);

        when(customerRepository.findByUserEmail("aria.deshmukh@scentiva.luxury")).thenReturn(Optional.of(customer));
        when(wishlistRepository.findByCustomerIdWithItems(10L)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(i -> i.getArgument(0));

        WishlistResponse response = wishlistService.removeFromWishlist("aria.deshmukh@scentiva.luxury", 100L);

        assertThat(response.getTotalCount()).isEqualTo(0);
        verify(wishlistItemRepository).delete(item);
    }
}
