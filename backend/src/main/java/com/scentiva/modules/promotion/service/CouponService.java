package com.scentiva.modules.promotion.service;

import com.scentiva.modules.promotion.dto.CouponCreateRequest;
import com.scentiva.modules.promotion.dto.CouponResponse;
import com.scentiva.modules.promotion.dto.CouponValidationResponse;
import com.scentiva.modules.promotion.model.Coupon;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {

    CouponValidationResponse validateAndCalculateDiscount(String code, BigDecimal subtotal);

    void recordRedemption(String code);

    Coupon getCouponByCode(String code);

    List<CouponResponse> getAllCoupons();

    CouponResponse createCoupon(CouponCreateRequest request);

    void deleteCoupon(Long id);
}
