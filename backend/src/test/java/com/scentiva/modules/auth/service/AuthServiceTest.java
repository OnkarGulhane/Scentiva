package com.scentiva.modules.auth.service;

import com.scentiva.common.exception.ConflictException;
import com.scentiva.modules.auth.dto.AuthResponse;
import com.scentiva.modules.auth.dto.RegisterRequest;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.model.UserStatus;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.auth.security.JwtTokenProvider;
import com.scentiva.modules.auth.service.impl.AuthServiceImpl;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstName("Aria")
                .lastName("Deshmukh")
                .email("aria.deshmukh@scentiva.luxury")
                .password("Scentiva@2026")
                .phone("+919876543210")
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new user and customer")
    void shouldRegisterNewUser() {
        when(userRepository.existsByEmailAndIsDeletedFalse(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedSecret");

        User savedUser = User.builder()
                .email("aria.deshmukh@scentiva.luxury")
                .passwordHash("hashedSecret")
                .role(Role.ROLE_CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();
        savedUser.setId(101L);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> {
            Customer c = i.getArgument(0);
            c.setId(201L);
            return c;
        });
        when(jwtTokenProvider.generateTokenFromUserIdAndEmail(eq(101L), eq("aria.deshmukh@scentiva.luxury"), eq("ROLE_CUSTOMER")))
                .thenReturn("mocked.jwt.token");
        when(jwtTokenProvider.getExpirationDurationMs()).thenReturn(86400000L);

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("aria.deshmukh@scentiva.luxury", response.getUser().getEmail());
        assertEquals("Aria Deshmukh", response.getUser().getFullName());
    }

    @Test
    @DisplayName("Should throw ConflictException if email is already registered")
    void shouldThrowConflictWhenEmailExists() {
        when(userRepository.existsByEmailAndIsDeletedFalse("aria.deshmukh@scentiva.luxury")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
