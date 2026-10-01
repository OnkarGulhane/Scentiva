package com.scentiva.modules.inventory.service;

import com.scentiva.modules.inventory.dto.WarehouseCreateRequest;
import com.scentiva.modules.inventory.dto.WarehouseResponse;

import java.util.List;

public interface WarehouseService {

    List<WarehouseResponse> getAllWarehouses();

    List<WarehouseResponse> getActiveWarehouses();

    WarehouseResponse getWarehouseById(Long id);

    WarehouseResponse createWarehouse(WarehouseCreateRequest request);

    WarehouseResponse updateWarehouse(Long id, WarehouseCreateRequest request);

    void deleteWarehouse(Long id);
}
