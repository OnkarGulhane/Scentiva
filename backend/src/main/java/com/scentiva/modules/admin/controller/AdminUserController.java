package com.scentiva.modules.admin.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.admin.dto.AdminUserResponse;
import com.scentiva.modules.admin.dto.AdminUserRoleUpdateRequest;
import com.scentiva.modules.admin.dto.AdminUserStatusUpdateRequest;
import com.scentiva.modules.admin.service.AdminUserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ROLE_SUPER_ADMIN', 'ADMIN', 'ROLE_ADMIN')")
@Tag(name = "Admin Staff & User Management", description = "Backoffice staff directory, RBAC role assignment and account controls")
public class AdminUserController {

    private final AdminUserManagementService adminUserService;

    @GetMapping
    @Operation(summary = "List staff & system users", description = "Retrieves all registered system users and assigned roles.")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllStaffUsers() {
        List<AdminUserResponse> users = adminUserService.getAllStaffUsers();
        return ResponseEntity.ok(ApiResponse.ok(users, "Users retrieved successfully"));
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Update user role", description = "Modifies RBAC role permissions for a staff member.")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserRoleUpdateRequest request) {
        AdminUserResponse response = adminUserService.updateUserRole(id, request.getRole());
        return ResponseEntity.ok(ApiResponse.ok(response, "User role updated successfully"));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update user account status", description = "Locks, activates or suspends a user account.")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserStatusUpdateRequest request) {
        AdminUserResponse response = adminUserService.updateUserStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok(response, "User status updated successfully"));
    }
}
