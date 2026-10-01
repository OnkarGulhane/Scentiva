package com.scentiva.modules.returns.controller;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.returns.dto.ReturnCreateRequest;
import com.scentiva.modules.returns.dto.ReturnResponse;
import com.scentiva.modules.returns.dto.ReturnStatusUpdateRequest;
import com.scentiva.modules.returns.model.ReturnStatus;
import com.scentiva.modules.returns.service.ReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/returns")
@RequiredArgsConstructor
@Tag(name = "Returns & Refunds Management", description = "Return request submission, courier pickup, inspection and automated refunds")
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping
    @Operation(summary = "Submit return request", description = "Customer requests return for items in a delivered/confirmed order.")
    public ResponseEntity<ApiResponse<ReturnResponse>> createReturn(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReturnCreateRequest request) {
        String email = userDetails.getUsername();
        ReturnResponse response = returnService.createReturn(email, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Return request submitted successfully"));
    }

    @GetMapping("/{returnNumber}")
    @Operation(summary = "Get return details", description = "Retrieves details of a return request including items and refund amount.")
    public ResponseEntity<ApiResponse<ReturnResponse>> getReturnByNumber(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String returnNumber) {
        String email = userDetails.getUsername();
        ReturnResponse response = returnService.getReturnByNumber(email, returnNumber);
        return ResponseEntity.ok(ApiResponse.ok(response, "Return details retrieved"));
    }

    @GetMapping
    @Operation(summary = "Get customer returns", description = "Retrieves paginated return requests submitted by customer.")
    public ResponseEntity<ApiPaginatedResponse<ReturnResponse>> getCustomerReturns(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = userDetails.getUsername();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ApiPaginatedResponse<ReturnResponse> response = returnService.getCustomerReturns(email, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{returnNumber}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'RETURNS_MANAGER', 'PRODUCT_MANAGER')")
    @Operation(summary = "Update return status (Admin)", description = "Advances return state (APPROVED, PICKED_UP, RECEIVED, REFUNDED).")
    public ResponseEntity<ApiResponse<ReturnResponse>> updateReturnStatus(
            @PathVariable String returnNumber,
            @Valid @RequestBody ReturnStatusUpdateRequest request) {
        ReturnResponse response = returnService.updateReturnStatus(returnNumber, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Return status updated successfully"));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'RETURNS_MANAGER', 'PRODUCT_MANAGER')")
    @Operation(summary = "Get all return requests (Admin)", description = "Lists all customer returns with optional status filter.")
    public ResponseEntity<ApiPaginatedResponse<ReturnResponse>> getAllReturns(
            @RequestParam(required = false) ReturnStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ApiPaginatedResponse<ReturnResponse> response = returnService.getAllReturns(status, pageable);
        return ResponseEntity.ok(response);
    }
}
