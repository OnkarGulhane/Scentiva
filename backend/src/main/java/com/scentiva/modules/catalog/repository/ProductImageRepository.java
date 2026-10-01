package com.scentiva.modules.catalog.repository;

import com.scentiva.modules.catalog.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdAndIsDeletedFalseOrderByDisplayOrderAsc(Long productId);

    List<ProductImage> findByVariantIdAndIsDeletedFalseOrderByDisplayOrderAsc(Long variantId);
}
