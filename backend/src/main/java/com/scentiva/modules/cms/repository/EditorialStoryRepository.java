package com.scentiva.modules.cms.repository;

import com.scentiva.modules.cms.model.EditorialStory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EditorialStoryRepository extends JpaRepository<EditorialStory, Long> {

    Optional<EditorialStory> findBySlugAndIsDeletedFalse(String slug);

    Page<EditorialStory> findByIsDeletedFalseOrderByPublishedAtDesc(Pageable pageable);

    List<EditorialStory> findByIsFeaturedTrueAndIsDeletedFalseOrderByPublishedAtDesc();

    @Query("SELECT s FROM EditorialStory s WHERE s.isDeleted = false AND s.publishedAt IS NOT NULL ORDER BY s.publishedAt DESC")
    List<EditorialStory> findPublishedStories();
}
