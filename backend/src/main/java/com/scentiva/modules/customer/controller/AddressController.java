package com.scentiva.modules.customer.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.customer.dto.AddressCreateRequest;
import com.scentiva.modules.customer.dto.AddressResponse;
import com.scentiva.modules.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/addresses")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Customer Address Book", description = "Shipping & Billing Address Management")
public class AddressController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Get address book", description = "Returns all active postal addresses for the customer.")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        List<AddressResponse> addresses = customerService.getAddresses(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(addresses, "Addresses retrieved successfully"));
    }

    @PostMapping
    @Operation(summary = "Add new address", description = "Adds a shipping/billing address to the address book.")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddressCreateRequest request) {
        AddressResponse address = customerService.addAddress(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(address, "Address added successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update address", description = "Updates details of an existing address.")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody AddressCreateRequest request) {
        AddressResponse address = customerService.updateAddress(userDetails.getUsername(), id, request);
        return ResponseEntity.ok(ApiResponse.ok(address, "Address updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete address", description = "Soft-deletes an address from the address book.")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        customerService.deleteAddress(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Address deleted successfully"));
    }

    @PutMapping("/{id}/default")
    @Operation(summary = "Set default address", description = "Marks the specified address as default.")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        AddressResponse address = customerService.setDefaultAddress(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok(address, "Address marked as default"));
    }
}
