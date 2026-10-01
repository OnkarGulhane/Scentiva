package com.scentiva.modules.catalog.service;

import com.scentiva.common.exception.ConflictException;
import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.catalog.dto.BrandCreateRequest;
import com.scentiva.modules.catalog.dto.BrandResponse;
import com.scentiva.modules.catalog.model.Brand;
import com.scentiva.modules.catalog.model.BrandTier;
import com.scentiva.modules.catalog.repository.BrandRepository;
import com.scentiva.modules.catalog.service.impl.BrandServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandServiceImpl brandService;

    private Brand brand;

    @BeforeEach
    void setUp() {
        brand = Brand.builder()
                .name("Chanel")
                .slug("chanel")
                .originCountry("France")
                .tier(BrandTier.HERITAGE_MAISON)
                .isActive(true)
                .build();
        brand.setId(10L);
    }

    @Test
    @DisplayName("Should retrieve brand by slug")
    void shouldGetBrandBySlug() {
        when(brandRepository.findBySlugAndIsDeletedFalse("chanel")).thenReturn(Optional.of(brand));

        BrandResponse response = brandService.getBrandBySlug("chanel");

        assertNotNull(response);
        assertEquals("Chanel", response.getName());
        assertEquals("chanel", response.getSlug());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when brand does not exist")
    void shouldThrowWhenBrandNotFound() {
        when(brandRepository.findBySlugAndIsDeletedFalse("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> brandService.getBrandBySlug("unknown"));
    }

    @Test
    @DisplayName("Should create brand successfully")
    void shouldCreateBrand() {
        BrandCreateRequest request = BrandCreateRequest.builder()
                .name("Tom Ford")
                .slug("tom-ford")
                .originCountry("USA")
                .tier(BrandTier.PRESTIGE)
                .build();

        when(brandRepository.existsBySlug("tom-ford")).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenAnswer(i -> {
            Brand b = i.getArgument(0);
            b.setId(20L);
            return b;
        });

        BrandResponse response = brandService.createBrand(request);

        assertNotNull(response);
        assertEquals("Tom Ford", response.getName());
        assertEquals(20L, response.getId());
    }

    @Test
    @DisplayName("Should throw ConflictException if brand slug already exists")
    void shouldThrowConflictWhenSlugExists() {
        BrandCreateRequest request = BrandCreateRequest.builder()
                .name("Chanel")
                .slug("chanel")
                .originCountry("France")
                .build();

        when(brandRepository.existsBySlug("chanel")).thenReturn(true);

        assertThrows(ConflictException.class, () -> brandService.createBrand(request));
    }
}
