package com.scentiva.modules.admin.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.admin.dto.AdminSettingsResponse;
import com.scentiva.modules.admin.dto.AdminSettingsUpdateRequest;
import com.scentiva.modules.admin.service.AdminSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/settings")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ROLE_SUPER_ADMIN', 'ADMIN', 'ROLE_ADMIN')")
@Tag(name = "Admin System & Store Settings", description = "Global shipping thresholds, tax rates, operational currency and maintenance modes")
public class AdminSettingsController {

    private final AdminSettingsService adminSettingsService;

    @GetMapping
    @Operation(summary = "Get system settings", description = "Retrieves global store operational parameters.")
    public ResponseEntity<ApiResponse<AdminSettingsResponse>> getSettings() {
        AdminSettingsResponse settings = adminSettingsService.getSettings();
        return ResponseEntity.ok(ApiResponse.ok(settings, "Settings retrieved successfully"));
    }

    @PutMapping
    @Operation(summary = "Update system settings", description = "Updates global shipping thresholds, tax percentages and store contacts.")
    public ResponseEntity<ApiResponse<AdminSettingsResponse>> updateSettings(
            @Valid @RequestBody AdminSettingsUpdateRequest request) {
        AdminSettingsResponse response = adminSettingsService.updateSettings(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Settings updated successfully"));
    }
}
