package com.scentiva.modules.admin.controller;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.admin.dto.AdminCustomerDetailResponse;
import com.scentiva.modules.admin.dto.AdminCustomerSummaryResponse;
import com.scentiva.modules.admin.dto.AdminUserStatusUpdateRequest;
import com.scentiva.modules.admin.service.AdminCustomerManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/customers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
@Tag(name = "Admin Customer Management", description = "Backoffice customer list, order histories, spend metrics and account access control")
public class AdminCustomerController {

    private final AdminCustomerManagementService adminCustomerService;

    @GetMapping
    @Operation(summary = "List all customers (Paginated)", description = "Retrieves paginated customer list with spend statistics and status.")
    public ResponseEntity<ApiPaginatedResponse<AdminCustomerSummaryResponse>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ApiPaginatedResponse<AdminCustomerSummaryResponse> response = adminCustomerService.getAllCustomers(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer full details", description = "Retrieves customer profile, saved addresses and full order history.")
    public ResponseEntity<ApiResponse<AdminCustomerDetailResponse>> getCustomerDetails(@PathVariable Long id) {
        AdminCustomerDetailResponse details = adminCustomerService.getCustomerDetails(id);
        return ResponseEntity.ok(ApiResponse.ok(details, "Customer details retrieved successfully"));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update customer account status", description = "Activates or suspends customer account access.")
    public ResponseEntity<ApiResponse<Void>> updateCustomerStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserStatusUpdateRequest request) {
        adminCustomerService.updateCustomerStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok(null, "Customer account status updated successfully"));
    }
}
