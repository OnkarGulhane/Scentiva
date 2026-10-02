package com.scentiva.modules.promotion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.promotion.dto.CouponCreateRequest;
import com.scentiva.modules.promotion.model.Coupon;
import com.scentiva.modules.promotion.model.DiscountType;
import com.scentiva.modules.promotion.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CouponRepository couponRepository;

    private Coupon coupon;

    @BeforeEach
    void setUp() {
        coupon = couponRepository.findByCodeIgnoreCaseAndIsDeletedFalse("WELCOME10")
                .orElseGet(() -> couponRepository.save(Coupon.builder()
                        .code("WELCOME10")
                        .discountType(DiscountType.PERCENTAGE)
                        .discountValue(new BigDecimal("10.00"))
                        .minOrderValue(new BigDecimal("500.00"))
                        .maxDiscount(new BigDecimal("200.00"))
                        .usageLimitGlobal(100)
                        .redemptionCount(0)
                        .isActive(true)
                        .expiresAt(LocalDateTime.now().plusDays(30))
                        .build()));
    }

    @Test
    @DisplayName("GET /api/v1/coupons/validate should validate coupon anonymously")
    void shouldValidateCouponPublic() throws Exception {
        mockMvc.perform(get("/api/v1/coupons/validate")
                        .param("code", "WELCOME10")
                        .param("subtotal", "1000.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.valid", is(true)))
                .andExpect(jsonPath("$.data.calculatedDiscount", is(100.0)));
    }

    @Test
    @DisplayName("GET /api/v1/coupons should return all coupons for ADMIN")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldGetAllCouponsForAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[?(@.code == 'WELCOME10')]").exists());
    }

    @Test
    @DisplayName("POST /api/v1/coupons should create new coupon for ADMIN")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldCreateCouponForAdmin() throws Exception {
        CouponCreateRequest request = CouponCreateRequest.builder()
                .code("SPRING25")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(new BigDecimal("25.00"))
                .minOrderValue(new BigDecimal("2000.00"))
                .maxDiscount(new BigDecimal("1000.00"))
                .usageLimitGlobal(50)
                .isActive(true)
                .expiresAt(LocalDateTime.now().plusDays(15))
                .build();

        mockMvc.perform(post("/api/v1/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.code", is("SPRING25")));
    }

    @Test
    @DisplayName("DELETE /api/v1/coupons/{id} should soft delete coupon")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldDeleteCouponForAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/coupons/" + coupon.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }
}
