package com.scentiva.modules.auth.repository;

import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.model.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should persist user and find by email successfully")
    void shouldPersistAndFindUser() {
        User user = User.builder()
                .email("connoisseur@scentiva.luxury")
                .passwordHash("$2a$12$eXampleHashValueForTestingPasswordSecurity1234567890")
                .role(Role.ROLE_CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("connoisseur@scentiva.luxury", savedUser.getEmail());
        assertEquals(Role.ROLE_CUSTOMER, savedUser.getRole());

        Optional<User> found = userRepository.findByEmailAndIsDeletedFalse("connoisseur@scentiva.luxury");
        assertTrue(found.isPresent());
        assertEquals(savedUser.getId(), found.get().getId());
    }

    @Test
    @DisplayName("existsByEmail should return true when user exists")
    void shouldCheckExistingEmail() {
        User user = User.builder()
                .email("admin@scentiva.luxury")
                .passwordHash("hash")
                .role(Role.ROLE_ADMIN)
                .build();
        userRepository.save(user);

        assertTrue(userRepository.existsByEmailAndIsDeletedFalse("admin@scentiva.luxury"));
        assertFalse(userRepository.existsByEmailAndIsDeletedFalse("nonexistent@scentiva.luxury"));
    }
}
