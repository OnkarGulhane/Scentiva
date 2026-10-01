package com.scentiva.modules.promotion.repository;

import com.scentiva.modules.promotion.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCodeIgnoreCaseAndIsDeletedFalse(String code);

    boolean existsByCodeIgnoreCase(String code);
}
