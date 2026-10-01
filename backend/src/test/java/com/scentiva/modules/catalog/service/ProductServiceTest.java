package com.scentiva.modules.catalog.service;

import com.scentiva.modules.catalog.dto.OlfactoryPyramidDto;
import com.scentiva.modules.catalog.dto.ProductCreateRequest;
import com.scentiva.modules.catalog.dto.ProductDetailResponse;
import com.scentiva.modules.catalog.dto.VariantCreateRequest;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.*;
import com.scentiva.modules.catalog.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private OlfactoryPyramidRepository olfactoryPyramidRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Brand brand;
    private Category category;

    @BeforeEach
    void setUp() {
        brand = Brand.builder()
                .name("Byredo")
                .slug("byredo")
                .originCountry("Sweden")
                .tier(BrandTier.NICHE_ATELIER)
                .build();
        brand.setId(1L);

        category = Category.builder()
                .name("Unisex")
                .slug("unisex")
                .build();
        category.setId(2L);
    }

    @Test
    @DisplayName("Should create product with olfactory pyramid and variants")
    void shouldCreateProductWithPyramid() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .brandId(1L)
                .categoryId(2L)
                .name("Gypsy Water")
                .slug("gypsy-water")
                .description("A glamorization of the Romany lifestyle.")
                .gender(GenderTarget.UNISEX)
                .olfactoryPyramid(OlfactoryPyramidDto.builder()
                        .fragranceFamily("Woody Aromatic")
                        .topNotes("Bergamot, Lemon, Pepper, Juniper Berries")
                        .heartNotes("Incense, Pine Needles, Orris")
                        .baseNotes("Amber, Vanilla, Sandalwood")
                        .longevityHours(8)
                        .build())
                .variants(List.of(
                        VariantCreateRequest.builder()
                                .sku("BYR-GW-EDP-50ML")
                                .volumeMl(50)
                                .concentration(Concentration.EDP)
                                .basePrice(new BigDecimal("18500.00"))
                                .build(),
                        VariantCreateRequest.builder()
                                .sku("BYR-GW-EDP-100ML")
                                .volumeMl(100)
                                .concentration(Concentration.EDP)
                                .basePrice(new BigDecimal("26000.00"))
                                .build()
                ))
                .build();

        when(productRepository.existsBySlug("gypsy-water")).thenReturn(false);
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(productVariantRepository.existsBySku(anyString())).thenReturn(false);

        when(productRepository.save(any(Product.class))).thenAnswer(i -> {
            Product p = i.getArgument(0);
            p.setId(50L);
            return p;
        });

        ProductDetailResponse response = productService.createProduct(request);

        assertNotNull(response);
        assertEquals("Gypsy Water", response.getName());
        assertEquals("gypsy-water", response.getSlug());
        assertEquals("Byredo", response.getBrand().getName());
        assertEquals("Unisex", response.getCategory().getName());
        assertNotNull(response.getOlfactoryPyramid());
        assertEquals("Woody Aromatic", response.getOlfactoryPyramid().getFragranceFamily());
        assertEquals(2, response.getVariants().size());
    }
}
