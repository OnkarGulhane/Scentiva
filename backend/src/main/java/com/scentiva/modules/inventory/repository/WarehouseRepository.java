package com.scentiva.modules.inventory.repository;

import com.scentiva.modules.inventory.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByCodeAndIsDeletedFalse(String code);

    boolean existsByCode(String code);

    List<Warehouse> findByIsActiveTrueAndIsDeletedFalse();
}
