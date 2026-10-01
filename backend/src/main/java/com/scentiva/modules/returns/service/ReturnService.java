package com.scentiva.modules.returns.service;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.returns.dto.ReturnCreateRequest;
import com.scentiva.modules.returns.dto.ReturnResponse;
import com.scentiva.modules.returns.dto.ReturnStatusUpdateRequest;
import com.scentiva.modules.returns.model.ReturnStatus;
import org.springframework.data.domain.Pageable;

public interface ReturnService {

    ReturnResponse createReturn(String email, ReturnCreateRequest request);

    ReturnResponse getReturnByNumber(String email, String returnNumber);

    ApiPaginatedResponse<ReturnResponse> getCustomerReturns(String email, Pageable pageable);

    ReturnResponse updateReturnStatus(String returnNumber, ReturnStatusUpdateRequest request);

    ApiPaginatedResponse<ReturnResponse> getAllReturns(ReturnStatus status, Pageable pageable);
}
