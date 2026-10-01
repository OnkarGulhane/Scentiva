package com.scentiva.modules.admin.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.admin.dto.AdminUserResponse;
import com.scentiva.modules.admin.service.AdminUserManagementService;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.model.UserStatus;
import com.scentiva.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserManagementServiceImpl implements AdminUserManagementService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllStaffUsers() {
        return userRepository.findAll().stream()
                .filter(u -> !u.isDeleted())
                .map(this::mapToUserResponse)
                .toList();
    }

    @Override
    @Transactional
    public AdminUserResponse updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setRole(newRole);
        user = userRepository.save(user);
        log.info("Updated user id={}, email={} role to {}", user.getId(), user.getEmail(), newRole);

        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse updateUserStatus(Long userId, UserStatus newStatus) {
        User user = userRepository.findById(userId)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setStatus(newStatus);
        user = userRepository.save(user);
        log.info("Updated user id={}, email={} status to {}", user.getId(), user.getEmail(), newStatus);

        return mapToUserResponse(user);
    }

    private AdminUserResponse mapToUserResponse(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
