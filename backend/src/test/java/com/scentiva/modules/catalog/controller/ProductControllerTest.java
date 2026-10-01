package com.scentiva.modules.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.catalog.model.Brand;
import com.scentiva.modules.catalog.model.BrandTier;
import com.scentiva.modules.catalog.model.Category;
import com.scentiva.modules.catalog.model.Concentration;
import com.scentiva.modules.catalog.model.GenderTarget;
import com.scentiva.modules.catalog.model.OlfactoryPyramid;
import com.scentiva.modules.catalog.model.Product;
import com.scentiva.modules.catalog.model.ProductVariant;
import com.scentiva.modules.catalog.repository.BrandRepository;
import com.scentiva.modules.catalog.repository.CategoryRepository;
import com.scentiva.modules.catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        if (!brandRepository.existsBySlug("maison-margiela")) {
            Brand brand = brandRepository.save(Brand.builder()
                    .name("Maison Margiela")
                    .slug("maison-margiela")
                    .originCountry("France")
                    .tier(BrandTier.NICHE_ATELIER)
                    .build());

            Category category = categoryRepository.save(Category.builder()
                    .name("Everyday Fresh")
                    .slug("everyday-fresh")
                    .build());

            Product product = Product.builder()
                    .brand(brand)
                    .category(category)
                    .name("Jazz Club Replica")
                    .slug("jazz-club-replica")
                    .description("Heady cocktails and cigars.")
                    .gender(GenderTarget.UNISEX)
                    .isFeatured(true)
                    .build();

            product.setOlfactoryPyramid(OlfactoryPyramid.builder()
                    .fragranceFamily("Warm & Spicy")
                    .topNotes("Pink Pepper, Primofiore Lemon, Neroli Oil")
                    .heartNotes("Rum Absolute, Clary Sage, Java Vetiver")
                    .baseNotes("Tobacco Leaf, Vanilla Bean, Styrax")
                    .build());

            product.addVariant(ProductVariant.builder()
                    .sku("MM-JC-EDT-100ML")
                    .volumeMl(100)
                    .concentration(Concentration.EDT)
                    .basePrice(new BigDecimal("14500.00"))
                    .build());

            productRepository.save(product);
        }
    }

    @Test
    @DisplayName("GET /api/v1/products should return 200 OK with paginated catalog")
    void shouldReturnPaginatedProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.items.length()", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("GET /api/v1/products/featured should return featured fragrance list")
    void shouldReturnFeaturedProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products/featured")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("GET /api/v1/products/{slug} should return product detail with olfactory pyramid")
    void shouldReturnProductDetailBySlug() throws Exception {
        mockMvc.perform(get("/api/v1/products/jazz-club-replica")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.slug", is("jazz-club-replica")))
                .andExpect(jsonPath("$.data.brand.name", is("Maison Margiela")))
                .andExpect(jsonPath("$.data.olfactoryPyramid.fragranceFamily", is("Warm & Spicy")));
    }

    @Test
    @DisplayName("GET /api/v1/brands should return active brands")
    void shouldReturnActiveBrands() throws Exception {
        mockMvc.perform(get("/api/v1/brands")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("GET /api/v1/categories should return active categories")
    void shouldReturnActiveCategories() throws Exception {
        mockMvc.perform(get("/api/v1/categories")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("POST /api/v1/products without admin authorization should return 401 Unauthorized")
    void shouldRejectUnauthenticatedProductCreation() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode", is("UNAUTHORIZED")));
    }
}
