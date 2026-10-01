package com.scentiva.modules.promotion.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.promotion.dto.CouponCreateRequest;
import com.scentiva.modules.promotion.dto.CouponResponse;
import com.scentiva.modules.promotion.dto.CouponValidationResponse;
import com.scentiva.modules.promotion.model.Coupon;
import com.scentiva.modules.promotion.model.DiscountType;
import com.scentiva.modules.promotion.repository.CouponRepository;
import com.scentiva.modules.promotion.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional(readOnly = true)
    public CouponValidationResponse validateAndCalculateDiscount(String code, BigDecimal subtotal) {
        if (code == null || code.trim().isEmpty()) {
            return CouponValidationResponse.builder()
                    .isValid(false)
                    .message("Coupon code is empty")
                    .calculatedDiscount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        Optional<Coupon> couponOpt = couponRepository.findByCodeIgnoreCaseAndIsDeletedFalse(code.trim());
        if (couponOpt.isEmpty()) {
            return CouponValidationResponse.builder()
                    .code(code)
                    .isValid(false)
                    .message("Invalid coupon code '" + code + "'")
                    .calculatedDiscount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        Coupon coupon = couponOpt.get();

        if (!coupon.isActive()) {
            return CouponValidationResponse.builder()
                    .code(coupon.getCode())
                    .isValid(false)
                    .message("Coupon code is inactive")
                    .calculatedDiscount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        if (coupon.isExpired()) {
            return CouponValidationResponse.builder()
                    .code(coupon.getCode())
                    .isValid(false)
                    .message("Coupon code has expired")
                    .calculatedDiscount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        if (coupon.isUsageLimitReached()) {
            return CouponValidationResponse.builder()
                    .code(coupon.getCode())
                    .isValid(false)
                    .message("Global usage limit reached for coupon")
                    .calculatedDiscount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        if (subtotal != null && subtotal.compareTo(coupon.getMinOrderValue()) < 0) {
            return CouponValidationResponse.builder()
                    .code(coupon.getCode())
                    .isValid(false)
                    .message(String.format("Minimum order value of ₹%s required for this coupon", coupon.getMinOrderValue()))
                    .calculatedDiscount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (subtotal != null) {
            if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
                discount = subtotal.multiply(coupon.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0) {
                    discount = coupon.getMaxDiscount();
                }
            } else if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                discount = coupon.getDiscountValue().min(subtotal);
            }
        }

        discount = discount.setScale(2, RoundingMode.HALF_UP);

        return CouponValidationResponse.builder()
                .code(coupon.getCode())
                .isValid(true)
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .calculatedDiscount(discount)
                .message("Coupon applied successfully")
                .build();
    }

    @Override
    @Transactional
    public void recordRedemption(String code) {
        if (code == null || code.trim().isEmpty()) return;

        couponRepository.findByCodeIgnoreCaseAndIsDeletedFalse(code.trim()).ifPresent(coupon -> {
            coupon.incrementRedemption();
            couponRepository.save(coupon);
            log.info("Recorded redemption for coupon={}, newRedemptionCount={}", coupon.getCode(), coupon.getRedemptionCount());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Coupon getCouponByCode(String code) {
        return couponRepository.findByCodeIgnoreCaseAndIsDeletedFalse(code.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", code));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .filter(c -> !c.isDeleted())
                .map(this::mapToCouponResponse)
                .toList();
    }

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponCreateRequest request) {
        if (couponRepository.findByCodeIgnoreCaseAndIsDeletedFalse(request.getCode().trim()).isPresent()) {
            throw new IllegalArgumentException("Coupon code '" + request.getCode().trim() + "' already exists");
        }

        Coupon coupon = Coupon.builder()
                .code(request.getCode().trim().toUpperCase())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderValue(request.getMinOrderValue() != null ? request.getMinOrderValue() : BigDecimal.ZERO)
                .maxDiscount(request.getMaxDiscount())
                .usageLimitGlobal(request.getUsageLimitGlobal())
                .usageLimitPerUser(request.getUsageLimitPerUser() != null ? request.getUsageLimitPerUser() : 1)
                .expiresAt(request.getExpiresAt())
                .isActive(request.isActive())
                .build();

        coupon = couponRepository.save(coupon);
        log.info("Created new coupon id={}, code={}", coupon.getId(), coupon.getCode());

        return mapToCouponResponse(coupon);
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));
        coupon.setDeleted(true);
        couponRepository.save(coupon);
        log.info("Soft deleted coupon id={}, code={}", coupon.getId(), coupon.getCode());
    }

    private CouponResponse mapToCouponResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderValue(coupon.getMinOrderValue())
                .maxDiscount(coupon.getMaxDiscount())
                .usageLimitGlobal(coupon.getUsageLimitGlobal())
                .usageLimitPerUser(coupon.getUsageLimitPerUser())
                .redemptionCount(coupon.getRedemptionCount())
                .expiresAt(coupon.getExpiresAt())
                .isActive(coupon.isActive())
                .createdAt(coupon.getCreatedAt())
                .build();
    }
}
