package com.scentiva.modules.customer.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.customer.dto.AddressCreateRequest;
import com.scentiva.modules.customer.dto.AddressResponse;
import com.scentiva.modules.customer.dto.CustomerProfileResponse;
import com.scentiva.modules.customer.dto.CustomerUpdateRequest;
import com.scentiva.modules.customer.model.Address;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.AddressRepository;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional(readOnly = true)
    public CustomerProfileResponse getProfile(String email) {
        Customer customer = getCustomerByEmail(email);
        return mapToCustomerResponse(customer);
    }

    @Override
    @Transactional
    public CustomerProfileResponse updateProfile(String email, CustomerUpdateRequest request) {
        Customer customer = getCustomerByEmail(email);

        customer.setFirstName(request.getFirstName().trim());
        customer.setLastName(request.getLastName().trim());
        if (request.getPhone() != null) {
            customer.setPhone(request.getPhone().trim());
        }

        customer = customerRepository.save(customer);
        log.info("Updated customer profile for email={}, customerId={}", email, customer.getId());
        return mapToCustomerResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(String email) {
        Customer customer = getCustomerByEmail(email);
        return addressRepository.findByCustomerIdAndIsDeletedFalse(customer.getId()).stream()
                .map(this::mapToAddressResponse)
                .toList();
    }

    @Override
    @Transactional
    public AddressResponse addAddress(String email, AddressCreateRequest request) {
        Customer customer = getCustomerByEmail(email);
        List<Address> existingAddresses = addressRepository.findByCustomerIdAndIsDeletedFalse(customer.getId());

        boolean makeDefault = Boolean.TRUE.equals(request.getIsDefault()) || existingAddresses.isEmpty();

        if (makeDefault) {
            existingAddresses.forEach(a -> {
                if (a.isDefault()) {
                    a.setDefault(false);
                    addressRepository.save(a);
                }
            });
        }

        Address address = Address.builder()
                .customer(customer)
                .fullName(request.getFullName().trim())
                .phone(request.getPhone().trim())
                .addressLine1(request.getAddressLine1().trim())
                .addressLine2(request.getAddressLine2() != null ? request.getAddressLine2().trim() : null)
                .landmark(request.getLandmark() != null ? request.getLandmark().trim() : null)
                .city(request.getCity().trim())
                .state(request.getState().trim())
                .postalCode(request.getPostalCode().trim())
                .country(request.getCountry() != null ? request.getCountry().trim() : "India")
                .isDefault(makeDefault)
                .addressType(request.getAddressType())
                .build();

        address = addressRepository.save(address);
        log.info("Added new address id={} for customer id={}", address.getId(), customer.getId());
        return mapToAddressResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(String email, Long addressId, AddressCreateRequest request) {
        Customer customer = getCustomerByEmail(email);
        Address address = addressRepository.findByIdAndCustomerIdAndIsDeletedFalse(addressId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        if (Boolean.TRUE.equals(request.getIsDefault()) && !address.isDefault()) {
            addressRepository.findByCustomerIdAndIsDeletedFalse(customer.getId()).forEach(a -> {
                if (a.isDefault()) {
                    a.setDefault(false);
                    addressRepository.save(a);
                }
            });
            address.setDefault(true);
        } else if (Boolean.FALSE.equals(request.getIsDefault())) {
            address.setDefault(false);
        }

        address.setFullName(request.getFullName().trim());
        address.setPhone(request.getPhone().trim());
        address.setAddressLine1(request.getAddressLine1().trim());
        address.setAddressLine2(request.getAddressLine2() != null ? request.getAddressLine2().trim() : null);
        address.setLandmark(request.getLandmark() != null ? request.getLandmark().trim() : null);
        address.setCity(request.getCity().trim());
        address.setState(request.getState().trim());
        address.setPostalCode(request.getPostalCode().trim());
        if (request.getCountry() != null) {
            address.setCountry(request.getCountry().trim());
        }
        if (request.getAddressType() != null) {
            address.setAddressType(request.getAddressType());
        }

        address = addressRepository.save(address);
        log.info("Updated address id={} for customer id={}", address.getId(), customer.getId());
        return mapToAddressResponse(address);
    }

    @Override
    @Transactional
    public void deleteAddress(String email, Long addressId) {
        Customer customer = getCustomerByEmail(email);
        Address address = addressRepository.findByIdAndCustomerIdAndIsDeletedFalse(addressId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        address.setDeleted(true);
        address.setDefault(false);
        addressRepository.save(address);
        log.info("Soft-deleted address id={} for customer id={}", addressId, customer.getId());
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(String email, Long addressId) {
        Customer customer = getCustomerByEmail(email);
        Address address = addressRepository.findByIdAndCustomerIdAndIsDeletedFalse(addressId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        addressRepository.findByCustomerIdAndIsDeletedFalse(customer.getId()).forEach(a -> {
            if (a.isDefault()) {
                a.setDefault(false);
                addressRepository.save(a);
            }
        });

        address.setDefault(true);
        address = addressRepository.save(address);
        log.info("Set address id={} as default for customer id={}", addressId, customer.getId());
        return mapToAddressResponse(address);
    }

    private Customer getCustomerByEmail(String email) {
        return customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));
    }

    private CustomerProfileResponse mapToCustomerResponse(Customer customer) {
        List<AddressResponse> addressResponses = addressRepository.findByCustomerIdAndIsDeletedFalse(customer.getId()).stream()
                .map(this::mapToAddressResponse)
                .toList();

        return CustomerProfileResponse.builder()
                .id(customer.getId())
                .userId(customer.getUser().getId())
                .email(customer.getUser().getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .loyaltyTier(customer.getLoyaltyTier())
                .addresses(addressResponses)
                .createdAt(customer.getCreatedAt())
                .build();
    }

    private AddressResponse mapToAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .customerId(address.getCustomer().getId())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.isDefault())
                .addressType(address.getAddressType())
                .createdAt(address.getCreatedAt())
                .build();
    }
}
