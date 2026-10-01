package com.scentiva.modules.catalog.repository;

import com.scentiva.modules.catalog.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CatalogRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Test
    @DisplayName("Should persist Brand, Category, Product with Olfactory Pyramid & Variants")
    void shouldPersistCompleteProductGraph() {
        // 1. Create Brand
        Brand dior = Brand.builder()
                .name("Dior")
                .slug("dior")
                .originCountry("France")
                .tier(BrandTier.HERITAGE_MAISON)
                .build();
        dior = brandRepository.save(dior);

        // 2. Create Category
        Category forHim = Category.builder()
                .name("For Him")
                .slug("for-him")
                .displayOrder(1)
                .build();
        forHim = categoryRepository.save(forHim);

        // 3. Create Product
        Product sauvage = Product.builder()
                .brand(dior)
                .category(forHim)
                .name("Sauvage Eau de Parfum")
                .slug("sauvage-edp")
                .description("A powerfully fresh trail, raw and noble all at once.")
                .gender(GenderTarget.FOR_HIM)
                .isFeatured(true)
                .build();

        // 4. Attach Olfactory Pyramid
        OlfactoryPyramid pyramid = OlfactoryPyramid.builder()
                .fragranceFamily("Aromatic Fougère")
                .topNotes("Calabrian Bergamot, Pepper")
                .heartNotes("Sichuan Pepper, Lavender, Star Anise, Nutmeg")
                .baseNotes("Ambroxan, Papua New Guinean Vanilla")
                .longevityHours(10)
                .build();
        sauvage.setOlfactoryPyramid(pyramid);

        // 5. Attach Variants (30ml & 100ml)
        ProductVariant variant30ml = ProductVariant.builder()
                .sku("DIOR-SAUV-EDP-30ML")
                .volumeMl(30)
                .concentration(Concentration.EDP)
                .basePrice(new BigDecimal("6500.00"))
                .build();

        ProductVariant variant100ml = ProductVariant.builder()
                .sku("DIOR-SAUV-EDP-100ML")
                .volumeMl(100)
                .concentration(Concentration.EDP)
                .basePrice(new BigDecimal("13500.00"))
                .salePrice(new BigDecimal("12500.00"))
                .build();

        sauvage.addVariant(variant30ml);
        sauvage.addVariant(variant100ml);

        Product savedProduct = productRepository.save(sauvage);
        assertNotNull(savedProduct.getId());

        // 6. Verify Fetch with Details
        Optional<Product> foundProduct = productRepository.findBySlugWithDetails("sauvage-edp");
        assertTrue(foundProduct.isPresent());
        assertEquals("Dior", foundProduct.get().getBrand().getName());
        assertEquals("For Him", foundProduct.get().getCategory().getName());
        assertNotNull(foundProduct.get().getOlfactoryPyramid());
        assertEquals("Ambroxan, Papua New Guinean Vanilla", foundProduct.get().getOlfactoryPyramid().getBaseNotes());

        // 7. Verify Variant Pricing
        Optional<ProductVariant> foundVariant100ml = productVariantRepository.findBySkuAndIsDeletedFalse("DIOR-SAUV-EDP-100ML");
        assertTrue(foundVariant100ml.isPresent());
        assertEquals(new BigDecimal("13500.00"), foundVariant100ml.get().getBasePrice());
        assertEquals(new BigDecimal("12500.00"), foundVariant100ml.get().getEffectivePrice());
    }
}
