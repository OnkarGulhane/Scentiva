package com.scentiva.modules.cart.service.impl;

import com.scentiva.common.exception.InsufficientStockException;
import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.cart.dto.AddToCartRequest;
import com.scentiva.modules.cart.dto.CartItemResponse;
import com.scentiva.modules.cart.dto.CartResponse;
import com.scentiva.modules.cart.model.Cart;
import com.scentiva.modules.cart.model.CartItem;
import com.scentiva.modules.cart.repository.CartItemRepository;
import com.scentiva.modules.cart.repository.CartRepository;
import com.scentiva.modules.cart.service.CartService;
import com.scentiva.modules.catalog.model.Product;
import com.scentiva.modules.catalog.model.ProductImage;
import com.scentiva.modules.catalog.model.ProductVariant;
import com.scentiva.modules.catalog.repository.ProductVariantRepository;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("2000.00");
    private static final BigDecimal STANDARD_DELIVERY_FEE = new BigDecimal("150.00");

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public CartResponse getCart(String email, String sessionId) {
        Cart cart = getOrCreateCart(email, sessionId);
        List<CartItem> items = cartItemRepository.findByCartIdAndIsDeletedFalse(cart.getId());
        return buildCartResponse(cart, items);
    }

    @Override
    @Transactional
    public CartResponse addToCart(String email, String sessionId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(email, sessionId);

        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .filter(v -> !v.isDeleted() && v.isActive())
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", request.getVariantId()));

        int availableStock = inventoryService.getTotalAvailableStock(variant.getId());
        if (availableStock < request.getQuantity()) {
            throw new InsufficientStockException(variant.getSku(), request.getQuantity(), availableStock);
        }

        BigDecimal unitPrice = variant.getEffectivePrice();

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndVariantIdAndIsDeletedFalse(cart.getId(), variant.getId());

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (availableStock < newQuantity) {
                throw new InsufficientStockException(variant.getSku(), newQuantity, availableStock);
            }
            existingItem.setQuantity(newQuantity);
            existingItem.setUnitPriceAtAddition(unitPrice);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .variant(variant)
                    .quantity(request.getQuantity())
                    .unitPriceAtAddition(unitPrice)
                    .build();
            cartItemRepository.save(newItem);
        }

        log.info("Added variant SKU={} to cart id={}", variant.getSku(), cart.getId());
        List<CartItem> items = cartItemRepository.findByCartIdAndIsDeletedFalse(cart.getId());
        return buildCartResponse(cart, items);
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(String email, String sessionId, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(email, sessionId);

        CartItem item = cartItemRepository.findByIdAndCartIdAndIsDeletedFalse(itemId, cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            int availableStock = inventoryService.getTotalAvailableStock(item.getVariant().getId());
            if (availableStock < quantity) {
                throw new InsufficientStockException(item.getVariant().getSku(), quantity, availableStock);
            }
            item.setQuantity(quantity);
            item.setUnitPriceAtAddition(item.getVariant().getEffectivePrice());
            cartItemRepository.save(item);
        }

        List<CartItem> items = cartItemRepository.findByCartIdAndIsDeletedFalse(cart.getId());
        return buildCartResponse(cart, items);
    }

    @Override
    @Transactional
    public CartResponse removeItem(String email, String sessionId, Long itemId) {
        Cart cart = getOrCreateCart(email, sessionId);

        CartItem item = cartItemRepository.findByIdAndCartIdAndIsDeletedFalse(itemId, cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        cartItemRepository.delete(item);
        log.info("Removed cart item id={} from cart id={}", itemId, cart.getId());
        List<CartItem> items = cartItemRepository.findByCartIdAndIsDeletedFalse(cart.getId());
        return buildCartResponse(cart, items);
    }

    @Override
    @Transactional
    public CartResponse clearCart(String email, String sessionId) {
        Cart cart = getOrCreateCart(email, sessionId);
        List<CartItem> items = cartItemRepository.findByCartIdAndIsDeletedFalse(cart.getId());
        cartItemRepository.deleteAll(items);
        log.info("Cleared cart id={}", cart.getId());
        return buildCartResponse(cart, new ArrayList<>());
    }

    @Override
    @Transactional
    public CartResponse mergeGuestCart(String email, String guestSessionId) {
        if (guestSessionId == null || guestSessionId.trim().isEmpty()) {
            return getCart(email, null);
        }

        Cart customerCart = getOrCreateCart(email, null);
        Optional<Cart> guestCartOpt = cartRepository.findBySessionIdWithItems(guestSessionId.trim());

        if (guestCartOpt.isPresent() && !guestCartOpt.get().getId().equals(customerCart.getId())) {
            Cart guestCart = guestCartOpt.get();
            List<CartItem> guestItems = cartItemRepository.findByCartIdAndIsDeletedFalse(guestCart.getId());

            for (CartItem guestItem : guestItems) {
                ProductVariant variant = guestItem.getVariant();
                Optional<CartItem> customerItemOpt = cartItemRepository.findByCartIdAndVariantIdAndIsDeletedFalse(customerCart.getId(), variant.getId());

                if (customerItemOpt.isPresent()) {
                    CartItem customerItem = customerItemOpt.get();
                    customerItem.setQuantity(customerItem.getQuantity() + guestItem.getQuantity());
                    customerItem.setUnitPriceAtAddition(variant.getEffectivePrice());
                    cartItemRepository.save(customerItem);
                } else {
                    CartItem newItem = CartItem.builder()
                            .cart(customerCart)
                            .variant(variant)
                            .quantity(guestItem.getQuantity())
                            .unitPriceAtAddition(variant.getEffectivePrice())
                            .build();
                    cartItemRepository.save(newItem);
                }
            }

            cartItemRepository.deleteAll(guestItems);
            guestCart.setDeleted(true);
            cartRepository.save(guestCart);
            log.info("Merged guest cart session={} into customer cart id={}", guestSessionId, customerCart.getId());
        }

        List<CartItem> items = cartItemRepository.findByCartIdAndIsDeletedFalse(customerCart.getId());
        return buildCartResponse(customerCart, items);
    }

    private Cart getOrCreateCart(String email, String sessionId) {
        if (email != null && !email.trim().isEmpty()) {
            Customer customer = customerRepository.findByUserEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));

            return cartRepository.findByCustomerIdAndIsDeletedFalse(customer.getId())
                    .orElseGet(() -> cartRepository.save(Cart.builder()
                            .customer(customer)
                            .items(new ArrayList<>())
                            .build()));
        } else if (sessionId != null && !sessionId.trim().isEmpty()) {
            return cartRepository.findBySessionIdAndCustomerIdIsNullAndIsDeletedFalse(sessionId.trim())
                    .orElseGet(() -> cartRepository.save(Cart.builder()
                            .sessionId(sessionId.trim())
                            .items(new ArrayList<>())
                            .build()));
        } else {
            throw new IllegalArgumentException("Either authenticated user email or guest sessionId must be provided");
        }
    }

    private CartResponse buildCartResponse(Cart cart, List<CartItem> items) {
        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : items) {
            ProductVariant variant = item.getVariant();
            Product product = variant.getProduct();

            BigDecimal unitPrice = variant.getEffectivePrice();
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())).setScale(2, RoundingMode.HALF_UP);
            subtotal = subtotal.add(itemTotal);
            totalItems += item.getQuantity();

            int availableStock = inventoryService.getTotalAvailableStock(variant.getId());
            boolean isAvailable = variant.isActive() && !variant.isDeleted() && availableStock >= item.getQuantity();

            String imageUrl = (product != null && product.getImages() != null)
                    ? product.getImages().stream()
                    .filter(ProductImage::isPrimary)
                    .findFirst()
                    .map(ProductImage::getImageUrl)
                    .orElseGet(() -> !product.getImages().isEmpty() ? product.getImages().get(0).getImageUrl() : null)
                    : null;

            itemResponses.add(CartItemResponse.builder()
                    .id(item.getId())
                    .variantId(variant.getId())
                    .sku(variant.getSku())
                    .productId(product != null ? product.getId() : null)
                    .productName(product != null ? product.getName() : null)
                    .productSlug(product != null ? product.getSlug() : null)
                    .brandName(product != null && product.getBrand() != null ? product.getBrand().getName() : null)
                    .concentration(variant.getConcentration())
                    .volumeMl(variant.getVolumeMl())
                    .imageUrl(imageUrl)
                    .unitPrice(unitPrice)
                    .quantity(item.getQuantity())
                    .itemTotal(itemTotal)
                    .isAvailable(isAvailable)
                    .availableStock(availableStock)
                    .build());
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal discountAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        boolean isFreeDelivery = totalItems == 0 || subtotal.compareTo(FREE_DELIVERY_THRESHOLD) >= 0;
        BigDecimal deliveryFee = (totalItems == 0 || isFreeDelivery)
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : STANDARD_DELIVERY_FEE.setScale(2, RoundingMode.HALF_UP);

        BigDecimal freeDeliveryDelta = (isFreeDelivery || totalItems == 0)
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : FREE_DELIVERY_THRESHOLD.subtract(subtotal).setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalTotal = subtotal.subtract(discountAmount).add(deliveryFee).add(taxAmount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        return CartResponse.builder()
                .id(cart.getId())
                .customerId(cart.getCustomer() != null ? cart.getCustomer().getId() : null)
                .sessionId(cart.getSessionId())
                .items(itemResponses)
                .totalItems(totalItems)
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .deliveryFee(deliveryFee)
                .taxAmount(taxAmount)
                .finalTotal(finalTotal)
                .isFreeDeliveryQualified(isFreeDelivery)
                .freeDeliveryThreshold(FREE_DELIVERY_THRESHOLD)
                .freeDeliveryDelta(freeDeliveryDelta)
                .build();
    }
}
