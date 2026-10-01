package com.scentiva.modules.admin.service;

import com.scentiva.modules.admin.dto.AdminUserResponse;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.model.UserStatus;
import com.scentiva.modules.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminUserManagementServiceTest {

    @Autowired
    private AdminUserManagementService adminUserService;

    @Autowired
    private UserRepository userRepository;

    private User staffUser;

    @BeforeEach
    void setUp() {
        staffUser = userRepository.save(User.builder()
                .email("staff.manager@scentiva.luxury")
                .passwordHash("hashed")
                .role(Role.ROLE_PRODUCT_MANAGER)
                .status(UserStatus.ACTIVE)
                .build());
    }

    @Test
    @DisplayName("Should list all staff and system users")
    void shouldGetAllStaffUsers() {
        List<AdminUserResponse> users = adminUserService.getAllStaffUsers();

        assertThat(users).isNotEmpty();
        assertThat(users.stream().anyMatch(u -> u.getEmail().equals("staff.manager@scentiva.luxury"))).isTrue();
    }

    @Test
    @DisplayName("Should update user RBAC role")
    void shouldUpdateUserRole() {
        AdminUserResponse updated = adminUserService.updateUserRole(staffUser.getId(), Role.ROLE_ADMIN);

        assertThat(updated.getRole()).isEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    @DisplayName("Should update user security status")
    void shouldUpdateUserStatus() {
        AdminUserResponse updated = adminUserService.updateUserStatus(staffUser.getId(), UserStatus.SUSPENDED);

        assertThat(updated.getStatus()).isEqualTo(UserStatus.SUSPENDED);
    }
}
