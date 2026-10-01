package com.scentiva.modules.admin.service;

import com.scentiva.modules.admin.dto.AdminUserResponse;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.UserStatus;

import java.util.List;

public interface AdminUserManagementService {

    List<AdminUserResponse> getAllStaffUsers();

    AdminUserResponse updateUserRole(Long userId, Role newRole);

    AdminUserResponse updateUserStatus(Long userId, UserStatus newStatus);
}
