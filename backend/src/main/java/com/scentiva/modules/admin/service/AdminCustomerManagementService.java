package com.scentiva.modules.admin.service;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.admin.dto.AdminCustomerDetailResponse;
import com.scentiva.modules.admin.dto.AdminCustomerSummaryResponse;
import com.scentiva.modules.auth.model.UserStatus;
import org.springframework.data.domain.Pageable;

public interface AdminCustomerManagementService {

    ApiPaginatedResponse<AdminCustomerSummaryResponse> getAllCustomers(Pageable pageable);

    AdminCustomerDetailResponse getCustomerDetails(Long customerId);

    void updateCustomerStatus(Long customerId, UserStatus status);
}
