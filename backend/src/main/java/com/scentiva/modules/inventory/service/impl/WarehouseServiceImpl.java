package com.scentiva.modules.inventory.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.inventory.dto.WarehouseCreateRequest;
import com.scentiva.modules.inventory.dto.WarehouseResponse;
import com.scentiva.modules.inventory.model.Warehouse;
import com.scentiva.modules.inventory.repository.WarehouseRepository;
import com.scentiva.modules.inventory.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .filter(w -> !w.isDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseResponse> getActiveWarehouses() {
        return warehouseRepository.findByIsActiveTrueAndIsDeletedFalse().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        return mapToResponse(warehouse);
    }

    @Override
    @Transactional
    public WarehouseResponse createWarehouse(WarehouseCreateRequest request) {
        if (warehouseRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Warehouse with code '" + request.getCode() + "' already exists");
        }

        Warehouse warehouse = Warehouse.builder()
                .code(request.getCode().trim().toUpperCase())
                .name(request.getName().trim())
                .city(request.getCity().trim())
                .state(request.getState().trim())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        warehouse = warehouseRepository.save(warehouse);
        log.info("Created warehouse: id={}, code={}", warehouse.getId(), warehouse.getCode());
        return mapToResponse(warehouse);
    }

    @Override
    @Transactional
    public WarehouseResponse updateWarehouse(Long id, WarehouseCreateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));

        if (!warehouse.getCode().equalsIgnoreCase(request.getCode()) && warehouseRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Warehouse with code '" + request.getCode() + "' already exists");
        }

        warehouse.setCode(request.getCode().trim().toUpperCase());
        warehouse.setName(request.getName().trim());
        warehouse.setCity(request.getCity().trim());
        warehouse.setState(request.getState().trim());
        if (request.getIsActive() != null) {
            warehouse.setActive(request.getIsActive());
        }

        warehouse = warehouseRepository.save(warehouse);
        log.info("Updated warehouse: id={}, code={}", warehouse.getId(), warehouse.getCode());
        return mapToResponse(warehouse);
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));

        warehouse.setDeleted(true);
        warehouse.setActive(false);
        warehouseRepository.save(warehouse);
        log.info("Soft deleted warehouse: id={}", id);
    }

    private WarehouseResponse mapToResponse(Warehouse warehouse) {
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .code(warehouse.getCode())
                .name(warehouse.getName())
                .city(warehouse.getCity())
                .state(warehouse.getState())
                .isActive(warehouse.isActive())
                .createdAt(warehouse.getCreatedAt())
                .build();
    }
}
