package com.scentiva.modules.promotion.repository;

import com.scentiva.modules.promotion.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Optional<Campaign> findBySlugAndIsDeletedFalse(String slug);

    List<Campaign> findByIsActiveTrueAndIsDeletedFalse();

    @Query("SELECT c FROM Campaign c WHERE c.isActive = true AND c.isDeleted = false AND :now BETWEEN c.startsAt AND c.endsAt ORDER BY c.startsAt DESC")
    List<Campaign> findActiveRunningCampaigns(LocalDateTime now);
}
