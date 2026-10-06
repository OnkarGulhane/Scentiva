package com.scentiva.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request payload for Google Identity Services OAuth login.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Google OAuth 2.0 Identity Token login payload")
public class GoogleLoginRequest {

    @NotBlank(message = "Google ID token is required")
    @Schema(description = "Cryptographically signed Google OAuth2 ID Token (JWT) issued by Google Identity Services", requiredMode = Schema.RequiredMode.REQUIRED)
    private String idToken;
}
