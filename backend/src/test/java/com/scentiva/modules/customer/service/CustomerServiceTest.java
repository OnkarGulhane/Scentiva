package com.scentiva.modules.customer.service;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.customer.dto.AddressCreateRequest;
import com.scentiva.modules.customer.dto.AddressResponse;
import com.scentiva.modules.customer.dto.CustomerProfileResponse;
import com.scentiva.modules.customer.dto.CustomerUpdateRequest;
import com.scentiva.modules.customer.model.Address;
import com.scentiva.modules.customer.model.AddressType;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.model.LoyaltyTier;
import com.scentiva.modules.customer.repository.AddressRepository;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.customer.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private User user;
    private Customer customer;
    private Address address;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("aria.deshmukh@scentiva.luxury")
                .passwordHash("hashed")
                .role(Role.ROLE_CUSTOMER)
                .build();
        user.setId(10L);

        customer = Customer.builder()
                .user(user)
                .firstName("Aria")
                .lastName("Deshmukh")
                .phone("+91 98765 43210")
                .loyaltyTier(LoyaltyTier.SILVER)
                .addresses(new ArrayList<>())
                .build();
        customer.setId(20L);
        customer.setDeleted(false);
        customer.setCreatedAt(LocalDateTime.now());

        address = Address.builder()
                .customer(customer)
                .fullName("Aria Deshmukh")
                .phone("+91 98765 43210")
                .addressLine1("101 Koregaon Park")
                .city("Pune")
                .state("Maharashtra")
                .postalCode("411001")
                .country("India")
                .isDefault(true)
                .addressType(AddressType.HOME)
                .build();
        address.setId(100L);
        address.setDeleted(false);
    }

    @Test
    @DisplayName("Should retrieve customer profile with address book")
    void shouldGetProfile() {
        when(customerRepository.findByUserEmail("aria.deshmukh@scentiva.luxury"))
                .thenReturn(Optional.of(customer));
        when(addressRepository.findByCustomerIdAndIsDeletedFalse(20L))
                .thenReturn(List.of(address));

        CustomerProfileResponse profile = customerService.getProfile("aria.deshmukh@scentiva.luxury");

        assertThat(profile).isNotNull();
        assertThat(profile.getFirstName()).isEqualTo("Aria");
        assertThat(profile.getFullName()).isEqualTo("Aria Deshmukh");
        assertThat(profile.getAddresses()).hasSize(1);
        assertThat(profile.getAddresses().get(0).getCity()).isEqualTo("Pune");
    }

    @Test
    @DisplayName("Should update customer profile details")
    void shouldUpdateProfile() {
        CustomerUpdateRequest request = CustomerUpdateRequest.builder()
                .firstName("Aria")
                .lastName("Kulkarni")
                .phone("+91 99999 88888")
                .build();

        when(customerRepository.findByUserEmail("aria.deshmukh@scentiva.luxury"))
                .thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        CustomerProfileResponse profile = customerService.updateProfile("aria.deshmukh@scentiva.luxury", request);

        assertThat(profile.getLastName()).isEqualTo("Kulkarni");
        assertThat(profile.getPhone()).isEqualTo("+91 99999 88888");
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("Should add new address and set as default if first address")
    void shouldAddNewAddress() {
        AddressCreateRequest request = AddressCreateRequest.builder()
                .fullName("Aria Deshmukh")
                .phone("+91 98765 43210")
                .addressLine1("B-404 Bandra West")
                .city("Mumbai")
                .state("Maharashtra")
                .postalCode("400050")
                .country("India")
                .isDefault(true)
                .addressType(AddressType.HOME)
                .build();

        when(customerRepository.findByUserEmail("aria.deshmukh@scentiva.luxury"))
                .thenReturn(Optional.of(customer));
        when(addressRepository.findByCustomerIdAndIsDeletedFalse(20L)).thenReturn(List.of(address));
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> {
            Address saved = i.getArgument(0);
            saved.setId(101L);
            return saved;
        });

        AddressResponse response = customerService.addAddress("aria.deshmukh@scentiva.luxury", request);

        assertThat(response).isNotNull();
        assertThat(response.getCity()).isEqualTo("Mumbai");
        assertThat(response.isDefault()).isTrue();
    }

    @Test
    @DisplayName("Should soft delete address")
    void shouldDeleteAddress() {
        when(customerRepository.findByUserEmail("aria.deshmukh@scentiva.luxury"))
                .thenReturn(Optional.of(customer));
        when(addressRepository.findByIdAndCustomerIdAndIsDeletedFalse(100L, 20L))
                .thenReturn(Optional.of(address));

        customerService.deleteAddress("aria.deshmukh@scentiva.luxury", 100L);

        assertThat(address.isDeleted()).isTrue();
        assertThat(address.isDefault()).isFalse();
        verify(addressRepository).save(address);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when address not found")
    void shouldThrowWhenAddressNotFound() {
        when(customerRepository.findByUserEmail("aria.deshmukh@scentiva.luxury"))
                .thenReturn(Optional.of(customer));
        when(addressRepository.findByIdAndCustomerIdAndIsDeletedFalse(999L, 20L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.deleteAddress("aria.deshmukh@scentiva.luxury", 999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
