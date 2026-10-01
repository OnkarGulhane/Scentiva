package com.scentiva.modules.promotion.repository;

import com.scentiva.modules.promotion.model.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {

    List<CouponRedemption> findByCustomerId(Long customerId);

    long countByCouponIdAndCustomerId(Long couponId, Long customerId);
}
