package com.scentiva.modules.inventory.service;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.inventory.dto.WarehouseCreateRequest;
import com.scentiva.modules.inventory.dto.WarehouseResponse;
import com.scentiva.modules.inventory.model.Warehouse;
import com.scentiva.modules.inventory.repository.WarehouseRepository;
import com.scentiva.modules.inventory.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouse = Warehouse.builder()
                .code("WH-PUN-01")
                .name("Pune Central Hub")
                .city("Pune")
                .state("Maharashtra")
                .isActive(true)
                .build();
        warehouse.setId(1L);
        warehouse.setDeleted(false);
        warehouse.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should retrieve active warehouses")
    void shouldGetActiveWarehouses() {
        when(warehouseRepository.findByIsActiveTrueAndIsDeletedFalse()).thenReturn(List.of(warehouse));

        List<WarehouseResponse> responses = warehouseService.getActiveWarehouses();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCode()).isEqualTo("WH-PUN-01");
        assertThat(responses.get(0).getCity()).isEqualTo("Pune");
    }

    @Test
    @DisplayName("Should get warehouse by id")
    void shouldGetWarehouseById() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        WarehouseResponse response = warehouseService.getWarehouseById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Pune Central Hub");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when warehouse not found")
    void shouldThrowWhenWarehouseNotFound() {
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> warehouseService.getWarehouseById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should create new warehouse")
    void shouldCreateWarehouse() {
        WarehouseCreateRequest request = WarehouseCreateRequest.builder()
                .code("wh-mum-01")
                .name("Mumbai Logistics Center")
                .city("Mumbai")
                .state("Maharashtra")
                .isActive(true)
                .build();

        when(warehouseRepository.existsByCode("wh-mum-01")).thenReturn(false);
        when(warehouseRepository.save(any(Warehouse.class))).thenAnswer(invocation -> {
            Warehouse saved = invocation.getArgument(0);
            saved.setId(2L);
            saved.setCreatedAt(LocalDateTime.now());
            return saved;
        });

        WarehouseResponse response = warehouseService.createWarehouse(request);

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("WH-MUM-01");
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when warehouse code already exists")
    void shouldThrowWhenWarehouseCodeExists() {
        WarehouseCreateRequest request = WarehouseCreateRequest.builder()
                .code("WH-PUN-01")
                .name("Duplicate Warehouse")
                .city("Pune")
                .state("Maharashtra")
                .build();

        when(warehouseRepository.existsByCode("WH-PUN-01")).thenReturn(true);

        assertThatThrownBy(() -> warehouseService.createWarehouse(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should soft delete warehouse")
    void shouldSoftDeleteWarehouse() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        warehouseService.deleteWarehouse(1L);

        assertThat(warehouse.isDeleted()).isTrue();
        assertThat(warehouse.isActive()).isFalse();
        verify(warehouseRepository).save(warehouse);
    }
}
