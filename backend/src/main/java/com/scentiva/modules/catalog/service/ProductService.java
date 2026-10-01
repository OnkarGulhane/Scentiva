package com.scentiva.modules.catalog.service;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.catalog.dto.ProductCreateRequest;
import com.scentiva.modules.catalog.dto.ProductDetailResponse;
import com.scentiva.modules.catalog.dto.ProductSummaryResponse;
import com.scentiva.modules.catalog.model.GenderTarget;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ApiPaginatedResponse<ProductSummaryResponse> getProducts(Long brandId, Long categoryId, GenderTarget gender, Pageable pageable);

    ProductDetailResponse getProductBySlug(String slug);

    ProductDetailResponse getProductById(Long id);

    List<ProductSummaryResponse> getFeaturedProducts();

    ApiPaginatedResponse<ProductSummaryResponse> searchProducts(String query, Pageable pageable);

    ProductDetailResponse createProduct(ProductCreateRequest request);

    ProductDetailResponse updateProduct(Long id, ProductCreateRequest request);

    void deleteProduct(Long id);
}
