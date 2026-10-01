package com.scentiva.modules.customer.repository;

import com.scentiva.modules.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUserIdAndIsDeletedFalse(Long userId);

    @Query("SELECT c FROM Customer c JOIN FETCH c.user u WHERE u.email = :email AND c.isDeleted = false")
    Optional<Customer> findByUserEmail(@Param("email") String email);

    Optional<Customer> findByPhoneAndIsDeletedFalse(String phone);

    boolean existsByUserId(Long userId);
}
