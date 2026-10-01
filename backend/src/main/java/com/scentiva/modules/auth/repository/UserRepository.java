package com.scentiva.modules.auth.repository;

import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for User Identity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByEmail(String email);

    boolean existsByEmailAndIsDeletedFalse(String email);

    boolean existsByEmail(String email);

    List<User> findByRoleAndIsDeletedFalse(Role role);
}
