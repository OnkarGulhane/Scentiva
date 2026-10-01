package com.scentiva.config;

import com.scentiva.modules.auth.security.CustomAccessDeniedHandler;
import com.scentiva.modules.auth.security.CustomUserDetailsService;
import com.scentiva.modules.auth.security.JwtAuthenticationEntryPoint;
import com.scentiva.modules.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 6 Configuration.
 * Configures stateless session policy, BCrypt password hashing, JWT filter integration, and endpoint authorization.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // Public Health & Diagnostics
                        .requestMatchers("/api/health", "/api/v1/health", "/actuator/**").permitAll()
                        // Swagger & OpenAPI Documentation
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Public Authentication Endpoints
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/logout").permitAll()
                        // Public Storefront Read Operations & Tracking
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalog/**", "/api/v1/products/**",
                                "/api/v1/brands/**", "/api/v1/categories/**", "/api/v1/stories/**",
                                "/api/v1/banners/**", "/api/v1/reviews/**", "/api/v1/warehouses/**",
                                "/api/v1/inventory/variant/**", "/api/v1/shipping/track/**",
                                "/api/v1/campaigns", "/api/v1/campaigns/**", "/api/v1/coupons/validate",
                                "/api/v1/seo/**").permitAll()
                        // Public Checkout Stock Reservation / Release & Shopping Bag
                        .requestMatchers(HttpMethod.POST, "/api/v1/inventory/reserve", "/api/v1/inventory/release").permitAll()
                        .requestMatchers("/api/v1/cart", "/api/v1/cart/items/**").permitAll()
                        // AI Discovery & Concierge Endpoints
                        .requestMatchers("/api/v1/ai/**").permitAll()
                        // Admin Operations
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "MANAGER", "PRODUCT_MANAGER", "MARKETING_MANAGER", "ORDER_MANAGER")
                        // Authenticated User Endpoints
                        .requestMatchers("/api/v1/auth/me", "/api/v1/auth/change-password").authenticated()
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
