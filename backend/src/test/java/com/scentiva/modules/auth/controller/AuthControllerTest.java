package com.scentiva.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.auth.dto.LoginRequest;
import com.scentiva.modules.auth.dto.RegisterRequest;
import com.scentiva.modules.auth.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("POST /api/v1/auth/register should create new customer and return 201 Created with JWT")
    void shouldRegisterCustomerSuccessfully() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Elena")
                .lastName("Veritas")
                .email("elena.veritas@scentiva.luxury")
                .password("HauteParfum@2026")
                .phone("+919876500000")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.statusCode", is(201)))
                .andExpect(jsonPath("$.data.user.email", is("elena.veritas@scentiva.luxury")))
                .andExpect(jsonPath("$.data.user.fullName", is("Elena Veritas")))
                .andExpect(jsonPath("$.data.tokenType", is("Bearer")));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login should authenticate registered customer and return 200 OK")
    void shouldLoginSuccessfully() throws Exception {
        // 1. Register first
        RegisterRequest register = RegisterRequest.builder()
                .firstName("Julian")
                .lastName("Sterling")
                .email("julian.sterling@scentiva.luxury")
                .password("SterlingPass@2026")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        // 2. Login
        LoginRequest login = LoginRequest.builder()
                .email("julian.sterling@scentiva.luxury")
                .password("SterlingPass@2026")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.user.email", is("julian.sterling@scentiva.luxury")));
    }

    @Test
    @DisplayName("GET /api/v1/auth/me should return 401 Unauthorized when no Bearer token is provided")
    void shouldRejectUnauthenticatedProfileAccess() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("UNAUTHORIZED")));
    }

    @Test
    @DisplayName("GET /api/v1/auth/me with valid Bearer token should return 200 OK and current profile")
    void shouldReturnProfileWithValidToken() throws Exception {
        // Register user
        RegisterRequest register = RegisterRequest.builder()
                .firstName("Sophia")
                .lastName("Laurent")
                .email("sophia.laurent@scentiva.luxury")
                .password("SophiaPass@2026")
                .build();

        String registerResponse = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(registerResponse).path("data").path("accessToken").asText();

        // Access protected endpoint
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.email", is("sophia.laurent@scentiva.luxury")))
                .andExpect(jsonPath("$.data.fullName", is("Sophia Laurent")));
    }
}
