package com.scentiva.modules.catalog.repository;

import com.scentiva.modules.catalog.model.GenderTarget;
import com.scentiva.modules.catalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.brand b " +
           "LEFT JOIN FETCH p.category c " +
           "LEFT JOIN FETCH p.olfactoryPyramid " +
           "WHERE p.slug = :slug AND p.isDeleted = false")
    Optional<Product> findBySlugWithDetails(@Param("slug") String slug);

    Optional<Product> findBySlugAndIsDeletedFalse(String slug);

    boolean existsBySlug(String slug);

    Page<Product> findByIsActiveTrueAndIsDeletedFalse(Pageable pageable);

    Page<Product> findByBrandIdAndIsActiveTrueAndIsDeletedFalse(Long brandId, Pageable pageable);

    Page<Product> findByCategoryIdAndIsActiveTrueAndIsDeletedFalse(Long categoryId, Pageable pageable);

    Page<Product> findByGenderAndIsActiveTrueAndIsDeletedFalse(GenderTarget gender, Pageable pageable);

    List<Product> findByIsFeaturedTrueAndIsActiveTrueAndIsDeletedFalse();

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.isDeleted = false " +
           "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);
}
