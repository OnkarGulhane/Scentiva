package com.scentiva.modules.review.repository;

import com.scentiva.modules.review.model.Review;
import com.scentiva.modules.review.model.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductIdAndStatusAndIsDeletedFalse(Long productId, ReviewStatus status, Pageable pageable);

    Page<Review> findByStatusAndIsDeletedFalse(ReviewStatus status, Pageable pageable);

    List<Review> findByCustomerIdAndIsDeletedFalse(Long customerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED' AND r.isDeleted = false")
    Double calculateAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED' AND r.isDeleted = false")
    Long countApprovedReviewsByProductId(@Param("productId") Long productId);

    Long countByStatusAndIsDeletedFalse(ReviewStatus status);
}
