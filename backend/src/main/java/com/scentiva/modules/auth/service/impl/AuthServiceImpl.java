package com.scentiva.modules.auth.service.impl;

import com.scentiva.common.exception.BadRequestException;
import com.scentiva.common.exception.ConflictException;
import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.auth.dto.*;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.model.UserStatus;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.auth.security.JwtTokenProvider;
import com.scentiva.modules.auth.service.AuthService;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.model.LoyaltyTier;
import com.scentiva.modules.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of Authentication & User Account Management Service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailAndIsDeletedFalse(normalizedEmail)) {
            throw new ConflictException("An account with email '" + normalizedEmail + "' already exists");
        }

        // 1. Create and Save Security User Account
        User user = User.builder()
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();
        user = userRepository.save(user);

        // 2. Create and Save Associated Customer Profile
        Customer customer = Customer.builder()
                .user(user)
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .loyaltyTier(LoyaltyTier.BRONZE)
                .build();
        customer = customerRepository.save(customer);

        log.info("Successfully registered new customer user: id={}, email={}", user.getId(), user.getEmail());

        // 3. Issue JWT Access Token
        String token = jwtTokenProvider.generateTokenFromUserIdAndEmail(user.getId(), user.getEmail(), user.getRole().name());

        UserProfileResponse userProfile = buildUserProfileResponse(user, customer);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresInMs(jwtTokenProvider.getExpirationDurationMs())
                .user(userProfile)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );

        User user = userRepository.findByEmailAndIsDeletedFalse(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", normalizedEmail));

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new BadRequestException("Your account has been suspended. Please contact concierge support.");
        }

        Customer customer = customerRepository.findByUserIdAndIsDeletedFalse(user.getId())
                .orElse(null);

        String token = jwtTokenProvider.generateToken(authentication);
        UserProfileResponse userProfile = buildUserProfileResponse(user, customer);

        log.info("User successfully authenticated: id={}, email={}", user.getId(), user.getEmail());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresInMs(jwtTokenProvider.getExpirationDurationMs())
                .user(userProfile)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Customer customer = customerRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElse(null);

        return buildUserProfileResponse(user, customer);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password does not match");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password successfully updated for user: id={}", userId);
    }

    private UserProfileResponse buildUserProfileResponse(User user, Customer customer) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .customerId(customer != null ? customer.getId() : null)
                .email(user.getEmail())
                .firstName(customer != null ? customer.getFirstName() : "")
                .lastName(customer != null ? customer.getLastName() : "")
                .fullName(customer != null ? customer.getFullName() : user.getEmail())
                .phone(customer != null ? customer.getPhone() : null)
                .role(user.getRole())
                .status(user.getStatus())
                .loyaltyTier(customer != null ? customer.getLoyaltyTier() : LoyaltyTier.BRONZE)
                .memberSince(user.getCreatedAt())
                .build();
    }
}
