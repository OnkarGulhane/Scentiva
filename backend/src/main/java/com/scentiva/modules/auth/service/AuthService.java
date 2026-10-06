package com.scentiva.modules.auth.service;

import com.scentiva.modules.auth.dto.AuthResponse;
import com.scentiva.modules.auth.dto.ChangePasswordRequest;
import com.scentiva.modules.auth.dto.GoogleLoginRequest;
import com.scentiva.modules.auth.dto.LoginRequest;
import com.scentiva.modules.auth.dto.RegisterRequest;
import com.scentiva.modules.auth.dto.UserProfileResponse;

/**
 * Authentication & Security Application Service.
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse googleLogin(GoogleLoginRequest request);

    UserProfileResponse getCurrentUserProfile(Long userId);

    void changePassword(Long userId, ChangePasswordRequest request);
}
