package com.scentiva.modules.customer.repository;

import com.scentiva.modules.customer.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByCustomerIdAndIsDeletedFalse(Long customerId);

    Optional<Address> findByIdAndCustomerIdAndIsDeletedFalse(Long id, Long customerId);

    Optional<Address> findByCustomerIdAndIsDefaultTrueAndIsDeletedFalse(Long customerId);
}
