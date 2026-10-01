package com.scentiva.modules.wishlist.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.catalog.model.Product;
import com.scentiva.modules.catalog.model.ProductImage;
import com.scentiva.modules.catalog.model.ProductVariant;
import com.scentiva.modules.catalog.repository.ProductVariantRepository;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.inventory.service.InventoryService;
import com.scentiva.modules.wishlist.dto.WishlistItemResponse;
import com.scentiva.modules.wishlist.dto.WishlistResponse;
import com.scentiva.modules.wishlist.model.Wishlist;
import com.scentiva.modules.wishlist.model.WishlistItem;
import com.scentiva.modules.wishlist.repository.WishlistItemRepository;
import com.scentiva.modules.wishlist.repository.WishlistRepository;
import com.scentiva.modules.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public WishlistResponse getWishlist(String email) {
        Wishlist wishlist = getOrCreateWishlist(email);
        return mapToWishlistResponse(wishlist);
    }

    @Override
    @Transactional
    public WishlistResponse addToWishlist(String email, Long variantId) {
        Wishlist wishlist = getOrCreateWishlist(email);

        ProductVariant variant = productVariantRepository.findById(variantId)
                .filter(v -> !v.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", variantId));

        Optional<WishlistItem> existingItem = wishlist.getItems().stream()
                .filter(i -> i.getVariant().getId().equals(variant.getId()))
                .findFirst();

        if (existingItem.isEmpty()) {
            WishlistItem item = WishlistItem.builder()
                    .wishlist(wishlist)
                    .variant(variant)
                    .build();
            wishlist.addItem(item);
            wishlistItemRepository.save(item);
            wishlist = wishlistRepository.save(wishlist);
            log.info("Added variant SKU={} to wishlist id={}", variant.getSku(), wishlist.getId());
        }

        return mapToWishlistResponse(wishlist);
    }

    @Override
    @Transactional
    public WishlistResponse removeFromWishlist(String email, Long variantId) {
        Wishlist wishlist = getOrCreateWishlist(email);

        Optional<WishlistItem> existingItem = wishlist.getItems().stream()
                .filter(i -> i.getVariant().getId().equals(variantId))
                .findFirst();

        if (existingItem.isPresent()) {
            WishlistItem item = existingItem.get();
            wishlist.removeItem(item);
            wishlistItemRepository.delete(item);
            wishlist = wishlistRepository.save(wishlist);
            log.info("Removed variant id={} from wishlist id={}", variantId, wishlist.getId());
        }

        return mapToWishlistResponse(wishlist);
    }

    @Override
    @Transactional
    public WishlistResponse clearWishlist(String email) {
        Wishlist wishlist = getOrCreateWishlist(email);
        wishlist.getItems().clear();
        wishlist = wishlistRepository.save(wishlist);
        log.info("Cleared wishlist id={}", wishlist.getId());
        return mapToWishlistResponse(wishlist);
    }

    private Wishlist getOrCreateWishlist(String email) {
        Customer customer = customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));

        return wishlistRepository.findByCustomerIdWithItems(customer.getId())
                .orElseGet(() -> wishlistRepository.save(Wishlist.builder()
                        .customer(customer)
                        .items(new ArrayList<>())
                        .build()));
    }

    private WishlistResponse mapToWishlistResponse(Wishlist wishlist) {
        List<WishlistItemResponse> itemResponses = new ArrayList<>();

        for (WishlistItem item : wishlist.getItems()) {
            ProductVariant variant = item.getVariant();
            if (variant == null || variant.isDeleted()) continue;

            Product product = variant.getProduct();
            int availableStock = inventoryService.getTotalAvailableStock(variant.getId());
            boolean inStock = variant.isActive() && !variant.isDeleted() && availableStock > 0;

            String imageUrl = (product != null && product.getImages() != null)
                    ? product.getImages().stream()
                    .filter(ProductImage::isPrimary)
                    .findFirst()
                    .map(ProductImage::getImageUrl)
                    .orElseGet(() -> !product.getImages().isEmpty() ? product.getImages().get(0).getImageUrl() : null)
                    : null;

            itemResponses.add(WishlistItemResponse.builder()
                    .id(item.getId())
                    .variantId(variant.getId())
                    .sku(variant.getSku())
                    .productId(product != null ? product.getId() : null)
                    .productName(product != null ? product.getName() : null)
                    .productSlug(product != null ? product.getSlug() : null)
                    .brandName(product != null && product.getBrand() != null ? product.getBrand().getName() : null)
                    .concentration(variant.getConcentration())
                    .volumeMl(variant.getVolumeMl())
                    .basePrice(variant.getBasePrice())
                    .salePrice(variant.getSalePrice())
                    .effectivePrice(variant.getEffectivePrice())
                    .imageUrl(imageUrl)
                    .isInStock(inStock)
                    .addedAt(item.getCreatedAt())
                    .build());
        }

        return WishlistResponse.builder()
                .id(wishlist.getId())
                .customerId(wishlist.getCustomer().getId())
                .items(itemResponses)
                .totalCount(itemResponses.size())
                .build();
    }
}
