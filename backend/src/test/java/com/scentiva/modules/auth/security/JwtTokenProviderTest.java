package com.scentiva.modules.auth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        String testSecret = "scentiva-test-secret-key-2026-luxury-perfumery-enterprise-jwt-token-key-minimum-512-bits-long-signature";
        long expirationMs = 3600000; // 1 Hour
        long refreshExpirationMs = 86400000; // 24 Hours
        jwtTokenProvider = new JwtTokenProvider(testSecret, expirationMs, refreshExpirationMs);
    }

    @Test
    @DisplayName("Should generate valid JWT token and parse claims accurately")
    void shouldGenerateAndValidateToken() {
        String token = jwtTokenProvider.generateTokenFromUserIdAndEmail(101L, "aria@scentiva.luxury", "ROLE_CUSTOMER");

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("aria@scentiva.luxury", jwtTokenProvider.getEmailFromToken(token));
        assertEquals(101L, jwtTokenProvider.getUserIdFromToken(token));
        assertEquals("ROLE_CUSTOMER", jwtTokenProvider.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Should reject invalid or malformed token")
    void shouldRejectInvalidToken() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.string"));
        assertFalse(jwtTokenProvider.validateToken(""));
    }
}
