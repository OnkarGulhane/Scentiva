package com.scentiva.common.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    @DisplayName("ApiResponse.ok should create standard success response")
    void testOkResponse() {
        ApiResponse<String> response = ApiResponse.ok("Sample Data", "Operation Successful");
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatusCode());
        assertEquals("Sample Data", response.getData());
        assertEquals("Operation Successful", response.getMessage());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("ApiResponse.created should create 201 Created response")
    void testCreatedResponse() {
        ApiResponse<Integer> response = ApiResponse.created(42, "Created Resource");
        assertTrue(response.isSuccess());
        assertEquals(201, response.getStatusCode());
        assertEquals(42, response.getData());
    }

    @Test
    @DisplayName("ApiPaginatedResponse should compute pagination metadata correctly")
    void testPaginatedResponse() {
        List<String> items = List.of("Dior Sauvage", "Creed Aventus", "Chanel Bleu");
        ApiPaginatedResponse<String> response = ApiPaginatedResponse.of(items, 0, 10, 25);

        assertEquals(3, response.getItems().size());
        assertEquals(0, response.getPage());
        assertEquals(10, response.getSize());
        assertEquals(25, response.getTotalElements());
        assertEquals(3, response.getTotalPages());
        assertTrue(response.isFirst());
        assertFalse(response.isLast());
    }
}
