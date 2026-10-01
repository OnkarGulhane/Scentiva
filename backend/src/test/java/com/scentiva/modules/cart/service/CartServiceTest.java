package com.scentiva.modules.cart.service;

import com.scentiva.common.exception.InsufficientStockException;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.cart.dto.AddToCartRequest;
import com.scentiva.modules.cart.dto.CartResponse;
import com.scentiva.modules.cart.model.Cart;
import com.scentiva.modules.cart.model.CartItem;
import com.scentiva.modules.cart.repository.CartItemRepository;
import com.scentiva.modules.cart.repository.CartRepository;
import com.scentiva.modules.cart.service.impl.CartServiceImpl;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.ProductVariantRepository;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Customer customer;
    private Product product;
    private ProductVariant variant;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = User.builder().email("aria.deshmukh@scentiva.luxury").role(Role.ROLE_CUSTOMER).build();
        user.setId(1L);

        customer = Customer.builder().user(user).firstName("Aria").lastName("Deshmukh").build();
        customer.setId(10L);

        Brand brand = Brand.builder().name("Dior").slug("dior").build();
        brand.setId(2L);

        Category category = Category.builder().name("Woody").slug("woody").build();
        category.setId(3L);

        product = Product.builder()
                .brand(brand)
                .category(category)
                .name("Sauvage Elixir")
                .slug("sauvage-elixir")
                .build();
        product.setId(20L);

        variant = ProductVariant.builder()
                .product(product)
                .sku("CD-SE-100")
                .volumeMl(100)
                .concentration(Concentration.EXTRAIT)
                .basePrice(new BigDecimal("1800.00"))
                .salePrice(new BigDecimal("1600.00"))
                .isActive(true)
                .build();
        variant.setId(100L);
        variant.setDeleted(false);

        cart = Cart.builder()
                .customer(customer)
                .items(new ArrayList<>())
                .build();
        cart.setId(50L);
        cart.setDeleted(false);

        cartItem = CartItem.builder()
                .cart(cart)
                .variant(variant)
                .quantity(1)
                .unitPriceAtAddition(new BigDecimal("1600.00"))
                .build();
        cartItem.setId(500L);
    }

    @Test
    @DisplayName("Should add item to cart and calculate standard delivery fee when subtotal < 2000")
    void shouldAddToCartWithDeliveryFee() {
        AddToCartRequest request = AddToCartRequest.builder()
                .variantId(100L)
                .quantity(1)
                .build();

        when(customerRepository.findByUserEmail("aria.deshmukh@scentiva.luxury")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(cart));
        when(productVariantRepository.findById(100L)).thenReturn(Optional.of(variant));
        when(inventoryService.getTotalAvailableStock(100L)).thenReturn(10);
        when(cartItemRepository.findByCartIdAndVariantIdAndIsDeletedFalse(50L, 100L)).thenReturn(Optional.empty());
        when(cartItemRepository.findByCartIdAndIsDeletedFalse(50L)).thenReturn(List.of(cartItem));

        CartResponse response = cartService.addToCart("aria.deshmukh@scentiva.luxury", null, request);

        assertThat(response).isNotNull();
        assertThat(response.getTotalItems()).isEqualTo(1);
        assertThat(response.getSubtotal()).isEqualByComparingTo("1600.00");
        assertThat(response.getDeliveryFee()).isEqualByComparingTo("150.00"); // Under 2000 threshold
        assertThat(response.isFreeDeliveryQualified()).isFalse();
        assertThat(response.getFreeDeliveryDelta()).isEqualByComparingTo("400.00");
        assertThat(response.getFinalTotal()).isEqualByComparingTo("1750.00"); // 1600 + 150
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should qualify for free delivery when subtotal >= 2000")
    void shouldQualifyForFreeDelivery() {
        AddToCartRequest request = AddToCartRequest.builder()
                .variantId(100L)
                .quantity(2)
                .build();

        CartItem doubleItem = CartItem.builder()
                .cart(cart)
                .variant(variant)
                .quantity(2) // 2 * 1600 = 3200
                .unitPriceAtAddition(new BigDecimal("1600.00"))
                .build();

        when(customerRepository.findByUserEmail("aria.deshmukh@scentiva.luxury")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(cart));
        when(productVariantRepository.findById(100L)).thenReturn(Optional.of(variant));
        when(inventoryService.getTotalAvailableStock(100L)).thenReturn(10);
        when(cartItemRepository.findByCartIdAndVariantIdAndIsDeletedFalse(50L, 100L)).thenReturn(Optional.empty());
        when(cartItemRepository.findByCartIdAndIsDeletedFalse(50L)).thenReturn(List.of(doubleItem));

        CartResponse response = cartService.addToCart("aria.deshmukh@scentiva.luxury", null, request);

        assertThat(response.getSubtotal()).isEqualByComparingTo("3200.00");
        assertThat(response.getDeliveryFee()).isEqualByComparingTo("0.00");
        assertThat(response.isFreeDeliveryQualified()).isTrue();
        assertThat(response.getFreeDeliveryDelta()).isEqualByComparingTo("0.00");
        assertThat(response.getFinalTotal()).isEqualByComparingTo("3200.00");
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when adding more items than available in stock")
    void shouldThrowWhenStockInsufficient() {
        AddToCartRequest request = AddToCartRequest.builder()
                .variantId(100L)
                .quantity(20)
                .build();

        when(customerRepository.findByUserEmail("aria.deshmukh@scentiva.luxury")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(cart));
        when(productVariantRepository.findById(100L)).thenReturn(Optional.of(variant));
        when(inventoryService.getTotalAvailableStock(100L)).thenReturn(5); // Only 5 available

        assertThatThrownBy(() -> cartService.addToCart("aria.deshmukh@scentiva.luxury", null, request))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("CD-SE-100");
    }

    @Test
    @DisplayName("Should clear all items from cart")
    void shouldClearCart() {
        when(customerRepository.findByUserEmail("aria.deshmukh@scentiva.luxury")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndIsDeletedFalse(50L)).thenReturn(List.of(cartItem));

        CartResponse response = cartService.clearCart("aria.deshmukh@scentiva.luxury", null);

        assertThat(response.getTotalItems()).isEqualTo(0);
        assertThat(response.getSubtotal()).isEqualByComparingTo("0.00");
        assertThat(response.getDeliveryFee()).isEqualByComparingTo("0.00");
        verify(cartItemRepository).deleteAll(List.of(cartItem));
    }
}
