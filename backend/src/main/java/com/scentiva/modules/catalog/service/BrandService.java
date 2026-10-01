package com.scentiva.modules.catalog.service;

import com.scentiva.modules.catalog.dto.BrandCreateRequest;
import com.scentiva.modules.catalog.dto.BrandResponse;

import java.util.List;

public interface BrandService {

    List<BrandResponse> getAllActiveBrands();

    BrandResponse getBrandBySlug(String slug);

    BrandResponse getBrandById(Long id);

    BrandResponse createBrand(BrandCreateRequest request);

    BrandResponse updateBrand(Long id, BrandCreateRequest request);

    void deleteBrand(Long id);
}
