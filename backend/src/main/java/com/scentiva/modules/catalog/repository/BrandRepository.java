package com.scentiva.modules.catalog.repository;

import com.scentiva.modules.catalog.model.Brand;
import com.scentiva.modules.catalog.model.BrandTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findBySlugAndIsDeletedFalse(String slug);

    Optional<Brand> findByNameAndIsDeletedFalse(String name);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    List<Brand> findByIsActiveTrueAndIsDeletedFalse();

    List<Brand> findByTierAndIsActiveTrueAndIsDeletedFalse(BrandTier tier);
}
