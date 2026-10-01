package com.scentiva.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication Success Response DTO with JWT Token and User Summary.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication Success Response")
public class AuthResponse {

    @Schema(description = "Stateless JWT Access Token")
    private String accessToken;

    @Schema(description = "Token Type", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Token expiration duration in milliseconds", example = "86400000")
    private long expiresInMs;

    @Schema(description = "Authenticated user profile summary")
    private UserProfileResponse user;
}
