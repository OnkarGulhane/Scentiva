package com.scentiva.modules.catalog.service.impl;

import com.scentiva.common.exception.ConflictException;
import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.catalog.dto.BrandCreateRequest;
import com.scentiva.modules.catalog.dto.BrandResponse;
import com.scentiva.modules.catalog.model.Brand;
import com.scentiva.modules.catalog.repository.BrandRepository;
import com.scentiva.modules.catalog.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllActiveBrands() {
        return brandRepository.findByIsActiveTrueAndIsDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandBySlug(String slug) {
        Brand brand = brandRepository.findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "slug", slug));
        return mapToResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
        return mapToResponse(brand);
    }

    @Override
    @Transactional
    public BrandResponse createBrand(BrandCreateRequest request) {
        String slug = request.getSlug().trim().toLowerCase();
        if (brandRepository.existsBySlug(slug)) {
            throw new ConflictException("A brand with slug '" + slug + "' already exists");
        }

        Brand brand = Brand.builder()
                .name(request.getName().trim())
                .slug(slug)
                .originCountry(request.getOriginCountry().trim())
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .coverImageUrl(request.getCoverImageUrl())
                .tier(request.getTier())
                .isActive(request.isActive())
                .build();

        brand = brandRepository.save(brand);
        log.info("Created brand: id={}, name={}", brand.getId(), brand.getName());
        return mapToResponse(brand);
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(Long id, BrandCreateRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));

        String slug = request.getSlug().trim().toLowerCase();
        if (!brand.getSlug().equals(slug) && brandRepository.existsBySlug(slug)) {
            throw new ConflictException("A brand with slug '" + slug + "' already exists");
        }

        brand.setName(request.getName().trim());
        brand.setSlug(slug);
        brand.setOriginCountry(request.getOriginCountry().trim());
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        brand.setCoverImageUrl(request.getCoverImageUrl());
        brand.setTier(request.getTier());
        brand.setActive(request.isActive());

        brand = brandRepository.save(brand);
        log.info("Updated brand: id={}, name={}", brand.getId(), brand.getName());
        return mapToResponse(brand);
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
        brand.setDeleted(true);
        brandRepository.save(brand);
        log.info("Soft-deleted brand: id={}", id);
    }

    private BrandResponse mapToResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .originCountry(brand.getOriginCountry())
                .description(brand.getDescription())
                .logoUrl(brand.getLogoUrl())
                .coverImageUrl(brand.getCoverImageUrl())
                .tier(brand.getTier())
                .isActive(brand.isActive())
                .build();
    }
}
