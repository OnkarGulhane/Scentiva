package com.scentiva.modules.cms.repository;

import com.scentiva.modules.cms.model.Banner;
import com.scentiva.modules.cms.model.BannerPlacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findByPlacementAndIsActiveTrueAndIsDeletedFalseOrderByDisplayOrderAsc(BannerPlacement placement);

    List<Banner> findByIsDeletedFalseOrderByDisplayOrderAsc();

    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND b.isDeleted = false AND (b.startsAt IS NULL OR b.startsAt <= CURRENT_TIMESTAMP) AND (b.endsAt IS NULL OR b.endsAt >= CURRENT_TIMESTAMP) ORDER BY b.displayOrder ASC")
    List<Banner> findCurrentlyActiveBanners();

    @Query("SELECT b FROM Banner b WHERE b.placement = :placement AND b.isActive = true AND b.isDeleted = false AND (b.startsAt IS NULL OR b.startsAt <= CURRENT_TIMESTAMP) AND (b.endsAt IS NULL OR b.endsAt >= CURRENT_TIMESTAMP) ORDER BY b.displayOrder ASC")
    List<Banner> findActiveBannersByPlacement(@Param("placement") BannerPlacement placement);
}
