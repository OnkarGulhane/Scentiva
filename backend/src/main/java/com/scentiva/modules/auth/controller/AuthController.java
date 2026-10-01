package com.scentiva.modules.auth.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.auth.dto.*;
import com.scentiva.modules.auth.security.UserPrincipal;
import com.scentiva.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication & Identity REST Controller.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication & Identity", description = "Registration, JWT Login, Profile & Password Management")
public class AuthController {

    private final AuthService authService;

    @Value("${scentiva.security.jwt.cookie-name:scentiva_access_token}")
    private String cookieName;

    @Value("${scentiva.security.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    @PostMapping("/register")
    @Operation(summary = "Register a new customer account", description = "Creates a new security user and customer profile, issuing a JWT access token.")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        setAuthCookie(response, authResponse.getAccessToken(), (int) (jwtExpirationMs / 1000));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(authResponse, "Account registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and issue JWT", description = "Validates credentials and returns JWT bearer token and customer summary.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        setAuthCookie(response, authResponse.getAccessToken(), (int) (jwtExpirationMs / 1000));
        return ResponseEntity.ok(ApiResponse.ok(authResponse, "Authentication successful"));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Get current authenticated user profile", description = "Returns full profile details of the current JWT bearer principal.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserProfileResponse profile = authService.getCurrentUserProfile(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok(profile, "User profile retrieved"));
    }

    @PostMapping("/change-password")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Change current user password", description = "Validates current password and updates to new password.")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(null, "Password updated successfully"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Clears the authentication HTTP-Only cookie.")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        setAuthCookie(response, "", 0);
        return ResponseEntity.ok(ApiResponse.ok(null, "Logged out successfully"));
    }

    private void setAuthCookie(HttpServletResponse response, String token, int maxAgeSeconds) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // In production TLS, set to true
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        response.addCookie(cookie);
    }
}
