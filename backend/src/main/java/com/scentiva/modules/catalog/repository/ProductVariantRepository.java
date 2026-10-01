package com.scentiva.modules.catalog.repository;

import com.scentiva.modules.catalog.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    Optional<ProductVariant> findBySkuAndIsDeletedFalse(String sku);

    boolean existsBySku(String sku);

    List<ProductVariant> findByProductIdAndIsActiveTrueAndIsDeletedFalse(Long productId);

    @Query("SELECT pv FROM ProductVariant pv LEFT JOIN FETCH pv.inventoryRecords ir WHERE pv.id = :id AND pv.isDeleted = false")
    Optional<ProductVariant> findByIdWithInventory(@Param("id") Long id);
}
