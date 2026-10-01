package com.scentiva.modules.customer.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.customer.dto.CustomerProfileResponse;
import com.scentiva.modules.customer.dto.CustomerUpdateRequest;
import com.scentiva.modules.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer/profile")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Customer Profile", description = "Customer Account Profile & Personal Details Management")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Get current customer profile", description = "Returns customer profile, loyalty tier, and address book.")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        CustomerProfileResponse profile = customerService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(profile, "Customer profile retrieved"));
    }

    @PutMapping
    @Operation(summary = "Update customer profile", description = "Updates personal contact details.")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CustomerUpdateRequest request) {
        CustomerProfileResponse updated = customerService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Profile updated successfully"));
    }
}
