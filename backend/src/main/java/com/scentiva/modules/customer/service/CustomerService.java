package com.scentiva.modules.customer.service;

import com.scentiva.modules.customer.dto.AddressCreateRequest;
import com.scentiva.modules.customer.dto.AddressResponse;
import com.scentiva.modules.customer.dto.CustomerProfileResponse;
import com.scentiva.modules.customer.dto.CustomerUpdateRequest;

import java.util.List;

public interface CustomerService {

    CustomerProfileResponse getProfile(String email);

    CustomerProfileResponse updateProfile(String email, CustomerUpdateRequest request);

    List<AddressResponse> getAddresses(String email);

    AddressResponse addAddress(String email, AddressCreateRequest request);

    AddressResponse updateAddress(String email, Long addressId, AddressCreateRequest request);

    void deleteAddress(String email, Long addressId);

    AddressResponse setDefaultAddress(String email, Long addressId);
}
