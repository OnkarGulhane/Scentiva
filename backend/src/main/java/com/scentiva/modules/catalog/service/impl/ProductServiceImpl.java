package com.scentiva.modules.catalog.service.impl;

import com.scentiva.common.exception.ConflictException;
import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.catalog.dto.*;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.*;
import com.scentiva.modules.catalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OlfactoryPyramidRepository olfactoryPyramidRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiPaginatedResponse<ProductSummaryResponse> getProducts(Long brandId, Long categoryId, GenderTarget gender, Pageable pageable) {
        Page<Product> page;

        if (brandId != null) {
            page = productRepository.findByBrandIdAndIsActiveTrueAndIsDeletedFalse(brandId, pageable);
        } else if (categoryId != null) {
            page = productRepository.findByCategoryIdAndIsActiveTrueAndIsDeletedFalse(categoryId, pageable);
        } else if (gender != null) {
            page = productRepository.findByGenderAndIsActiveTrueAndIsDeletedFalse(gender, pageable);
        } else {
            page = productRepository.findByIsActiveTrueAndIsDeletedFalse(pageable);
        }

        List<ProductSummaryResponse> summaries = page.getContent()
                .stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        return ApiPaginatedResponse.of(summaries, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlugWithDetails(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
        return mapToDetail(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToDetail(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrueAndIsActiveTrueAndIsDeletedFalse()
                .stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiPaginatedResponse<ProductSummaryResponse> searchProducts(String query, Pageable pageable) {
        Page<Product> page = productRepository.searchProducts(query, pageable);
        List<ProductSummaryResponse> summaries = page.getContent()
                .stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        return ApiPaginatedResponse.of(summaries, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Override
    @Transactional
    public ProductDetailResponse createProduct(ProductCreateRequest request) {
        String slug = request.getSlug().trim().toLowerCase();
        if (productRepository.existsBySlug(slug)) {
            throw new ConflictException("A product with slug '" + slug + "' already exists");
        }

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", request.getBrandId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = Product.builder()
                .brand(brand)
                .category(category)
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription())
                .gender(request.getGender())
                .isFeatured(request.isFeatured())
                .isActive(request.isActive())
                .build();

        // 1. Olfactory Pyramid
        if (request.getOlfactoryPyramid() != null) {
            OlfactoryPyramid pyramid = OlfactoryPyramid.builder()
                    .fragranceFamily(request.getOlfactoryPyramid().getFragranceFamily())
                    .topNotes(request.getOlfactoryPyramid().getTopNotes())
                    .heartNotes(request.getOlfactoryPyramid().getHeartNotes())
                    .baseNotes(request.getOlfactoryPyramid().getBaseNotes())
                    .sillageRating(request.getOlfactoryPyramid().getSillageRating() != null ? request.getOlfactoryPyramid().getSillageRating() : "MODERATE")
                    .longevityHours(request.getOlfactoryPyramid().getLongevityHours() != null ? request.getOlfactoryPyramid().getLongevityHours() : 8)
                    .build();
            product.setOlfactoryPyramid(pyramid);
        }

        // 2. Product Variants
        for (VariantCreateRequest vReq : request.getVariants()) {
            if (productVariantRepository.existsBySku(vReq.getSku())) {
                throw new ConflictException("Variant SKU '" + vReq.getSku() + "' is already in use");
            }

            ProductVariant variant = ProductVariant.builder()
                    .sku(vReq.getSku().trim())
                    .volumeMl(vReq.getVolumeMl())
                    .concentration(vReq.getConcentration())
                    .basePrice(vReq.getBasePrice())
                    .salePrice(vReq.getSalePrice())
                    .weightGrams(vReq.getWeightGrams())
                    .isActive(vReq.isActive())
                    .build();
            product.addVariant(variant);
        }

        product = productRepository.save(product);
        log.info("Created product: id={}, name={}, variants={}", product.getId(), product.getName(), product.getVariants().size());

        return mapToDetail(product);
    }

    @Override
    @Transactional
    public ProductDetailResponse updateProduct(Long id, ProductCreateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        String slug = request.getSlug().trim().toLowerCase();
        if (!product.getSlug().equals(slug) && productRepository.existsBySlug(slug)) {
            throw new ConflictException("A product with slug '" + slug + "' already exists");
        }

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", request.getBrandId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        product.setBrand(brand);
        product.setCategory(category);
        product.setName(request.getName().trim());
        product.setSlug(slug);
        product.setDescription(request.getDescription());
        product.setGender(request.getGender());
        product.setFeatured(request.isFeatured());
        product.setActive(request.isActive());

        if (request.getOlfactoryPyramid() != null && product.getOlfactoryPyramid() != null) {
            product.getOlfactoryPyramid().setFragranceFamily(request.getOlfactoryPyramid().getFragranceFamily());
            product.getOlfactoryPyramid().setTopNotes(request.getOlfactoryPyramid().getTopNotes());
            product.getOlfactoryPyramid().setHeartNotes(request.getOlfactoryPyramid().getHeartNotes());
            product.getOlfactoryPyramid().setBaseNotes(request.getOlfactoryPyramid().getBaseNotes());
            product.getOlfactoryPyramid().setSillageRating(request.getOlfactoryPyramid().getSillageRating());
            product.getOlfactoryPyramid().setLongevityHours(request.getOlfactoryPyramid().getLongevityHours());
        }

        product = productRepository.save(product);
        log.info("Updated product: id={}, name={}", product.getId(), product.getName());
        return mapToDetail(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setDeleted(true);
        productRepository.save(product);
        log.info("Soft-deleted product: id={}", id);
    }

    private ProductSummaryResponse mapToSummary(Product product) {
        List<ProductVariant> activeVariants = product.getVariants().stream()
                .filter(ProductVariant::isActive)
                .toList();

        BigDecimal minPrice = activeVariants.stream()
                .map(ProductVariant::getEffectivePrice)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal maxPrice = activeVariants.stream()
                .map(ProductVariant::getEffectivePrice)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        Concentration primaryConcentration = activeVariants.stream()
                .map(ProductVariant::getConcentration)
                .findFirst()
                .orElse(Concentration.EDP);

        String primaryImageUrl = product.getImages().stream()
                .filter(ProductImage::isPrimary)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(product.getImages().isEmpty() ? null : product.getImages().get(0).getImageUrl());

        boolean inStock = activeVariants.stream().anyMatch(v -> v.getTotalAvailableQuantity() > 0);

        return ProductSummaryResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .brandName(product.getBrand().getName())
                .brandSlug(product.getBrand().getSlug())
                .categoryName(product.getCategory().getName())
                .categorySlug(product.getCategory().getSlug())
                .gender(product.getGender())
                .fragranceFamily(product.getOlfactoryPyramid() != null ? product.getOlfactoryPyramid().getFragranceFamily() : "Haute Parfumerie")
                .primaryConcentration(primaryConcentration)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .primaryImageUrl(primaryImageUrl)
                .isFeatured(product.isFeatured())
                .inStock(inStock)
                .variantCount(activeVariants.size())
                .build();
    }

    private ProductDetailResponse mapToDetail(Product product) {
        BrandResponse brandDto = BrandResponse.builder()
                .id(product.getBrand().getId())
                .name(product.getBrand().getName())
                .slug(product.getBrand().getSlug())
                .originCountry(product.getBrand().getOriginCountry())
                .description(product.getBrand().getDescription())
                .logoUrl(product.getBrand().getLogoUrl())
                .coverImageUrl(product.getBrand().getCoverImageUrl())
                .tier(product.getBrand().getTier())
                .isActive(product.getBrand().isActive())
                .build();

        CategoryResponse categoryDto = CategoryResponse.builder()
                .id(product.getCategory().getId())
                .name(product.getCategory().getName())
                .slug(product.getCategory().getSlug())
                .description(product.getCategory().getDescription())
                .imageUrl(product.getCategory().getImageUrl())
                .displayOrder(product.getCategory().getDisplayOrder())
                .isActive(product.getCategory().isActive())
                .build();

        OlfactoryPyramidDto pyramidDto = null;
        if (product.getOlfactoryPyramid() != null) {
            pyramidDto = OlfactoryPyramidDto.builder()
                    .fragranceFamily(product.getOlfactoryPyramid().getFragranceFamily())
                    .topNotes(product.getOlfactoryPyramid().getTopNotes())
                    .heartNotes(product.getOlfactoryPyramid().getHeartNotes())
                    .baseNotes(product.getOlfactoryPyramid().getBaseNotes())
                    .sillageRating(product.getOlfactoryPyramid().getSillageRating())
                    .longevityHours(product.getOlfactoryPyramid().getLongevityHours())
                    .build();
        }

        List<ProductVariantDto> variantDtos = product.getVariants().stream()
                .map(v -> ProductVariantDto.builder()
                        .id(v.getId())
                        .sku(v.getSku())
                        .volumeMl(v.getVolumeMl())
                        .concentration(v.getConcentration())
                        .basePrice(v.getBasePrice())
                        .salePrice(v.getSalePrice())
                        .effectivePrice(v.getEffectivePrice())
                        .weightGrams(v.getWeightGrams())
                        .isActive(v.isActive())
                        .availableStock(v.getTotalAvailableQuantity())
                        .build())
                .collect(Collectors.toList());

        List<ProductImageDto> imageDtos = product.getImages().stream()
                .map(img -> ProductImageDto.builder()
                        .id(img.getId())
                        .variantId(img.getVariant() != null ? img.getVariant().getId() : null)
                        .imageUrl(img.getImageUrl())
                        .altText(img.getAltText())
                        .isPrimary(img.isPrimary())
                        .displayOrder(img.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());

        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .gender(product.getGender())
                .isFeatured(product.isFeatured())
                .isActive(product.isActive())
                .brand(brandDto)
                .category(categoryDto)
                .olfactoryPyramid(pyramidDto)
                .variants(variantDtos)
                .images(imageDtos)
                .build();
    }
}
