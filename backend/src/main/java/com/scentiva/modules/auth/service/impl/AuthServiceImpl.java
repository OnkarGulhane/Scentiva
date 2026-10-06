package com.scentiva.modules.auth.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

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
    private final com.scentiva.modules.notification.service.EmailService emailService;

    @Value("${scentiva.security.google.client-id:}")
    private String googleClientId;

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

        try {
            emailService.sendWelcomeEmail(user.getEmail(), customer.getFirstName(), customer.getLoyaltyTier().name());
        } catch (Exception ex) {
            log.warn("Could not dispatch welcome email to {}: {}", user.getEmail(), ex.getMessage());
        }

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
    @Transactional
    public AuthResponse googleLogin(GoogleLoginRequest request) {
        if (request.getIdToken() == null || request.getIdToken().trim().isEmpty()) {
            throw new BadRequestException("Google ID token cannot be empty");
        }

        GoogleIdToken idToken = verifyGoogleToken(request.getIdToken().trim());
        if (idToken == null) {
            throw new BadRequestException("Invalid or expired Google authentication token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Google account email could not be verified");
        }
        String normalizedEmail = email.trim().toLowerCase();

        String givenName = (String) payload.get("given_name");
        String familyName = (String) payload.get("family_name");
        String name = (String) payload.get("name");

        if (givenName == null || givenName.trim().isEmpty()) {
            if (name != null && !name.trim().isEmpty()) {
                String[] parts = name.trim().split("\\s+", 2);
                givenName = parts[0];
                familyName = parts.length > 1 ? parts[1] : "";
            } else {
                givenName = "Privé";
                familyName = "Client";
            }
        }
        if (familyName == null) {
            familyName = "";
        }

        final String finalGivenName = givenName.trim();
        final String finalFamilyName = familyName.trim();

        // Find or create User
        User user = userRepository.findByEmailAndIsDeletedFalse(normalizedEmail)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(normalizedEmail)
                            .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .role(Role.ROLE_CUSTOMER)
                            .status(UserStatus.ACTIVE)
                            .build();
                    newUser = userRepository.save(newUser);

                    Customer newCustomer = Customer.builder()
                            .user(newUser)
                            .firstName(finalGivenName)
                            .lastName(finalFamilyName)
                            .loyaltyTier(LoyaltyTier.BRONZE)
                            .build();
                    customerRepository.save(newCustomer);

                    log.info("Created new customer account from Google OAuth: id={}, email={}", newUser.getId(), normalizedEmail);
                    try {
                        emailService.sendWelcomeEmail(normalizedEmail, finalGivenName, LoyaltyTier.BRONZE.name());
                    } catch (Exception ex) {
                        log.warn("Could not dispatch Google welcome email to {}: {}", normalizedEmail, ex.getMessage());
                    }
                    return newUser;
                });

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new BadRequestException("Your account has been suspended. Please contact concierge support.");
        }

        Customer customer = customerRepository.findByUserIdAndIsDeletedFalse(user.getId()).orElse(null);

        String token = jwtTokenProvider.generateTokenFromUserIdAndEmail(user.getId(), user.getEmail(), user.getRole().name());
        UserProfileResponse userProfile = buildUserProfileResponse(user, customer);

        log.info("User successfully authenticated via Google OAuth: id={}, email={}", user.getId(), user.getEmail());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresInMs(jwtTokenProvider.getExpirationDurationMs())
                .user(userProfile)
                .build();
    }

    private GoogleIdToken verifyGoogleToken(String tokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
            return verifier.verify(tokenString);
        } catch (Exception e) {
            log.error("Google ID Token verification failed: {}", e.getMessage());
            throw new BadRequestException("Google ID Token verification failed: " + e.getMessage());
        }
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
