package com.scentiva.modules.catalog.repository;

import com.scentiva.modules.catalog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlugAndIsDeletedFalse(String slug);

    boolean existsBySlug(String slug);

    List<Category> findByIsActiveTrueAndIsDeletedFalseOrderByDisplayOrderAsc();
}
