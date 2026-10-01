package com.scentiva.modules.customer.repository;

import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.customer.model.Address;
import com.scentiva.modules.customer.model.AddressType;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.model.LoyaltyTier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CustomerAddressRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    @DisplayName("Should persist Customer and cascade Address records cleanly")
    void shouldPersistCustomerWithAddresses() {
        User user = User.builder()
                .email("shopper@scentiva.luxury")
                .passwordHash("hashedPass")
                .role(Role.ROLE_CUSTOMER)
                .build();
        user = userRepository.save(user);

        Customer customer = Customer.builder()
                .user(user)
                .firstName("Aria")
                .lastName("Deshmukh")
                .phone("+919876543210")
                .loyaltyTier(LoyaltyTier.VIP_CONNOISSEUR)
                .build();

        Address address = Address.builder()
                .fullName("Aria Deshmukh")
                .phone("+919876543210")
                .addressLine1("74 Heritage Boulevard")
                .addressLine2("Atelier Penthouse 4B")
                .landmark("Opposite Royal Opera")
                .city("Pune")
                .state("Maharashtra")
                .postalCode("411001")
                .country("India")
                .isDefault(true)
                .addressType(AddressType.HOME)
                .build();

        customer.addAddress(address);
        Customer savedCustomer = customerRepository.save(customer);

        assertNotNull(savedCustomer.getId());
        assertEquals("Aria Deshmukh", savedCustomer.getFullName());
        assertEquals(1, savedCustomer.getAddresses().size());

        Optional<Customer> foundByEmail = customerRepository.findByUserEmail("shopper@scentiva.luxury");
        assertTrue(foundByEmail.isPresent());
        assertEquals("Aria", foundByEmail.get().getFirstName());

        List<Address> customerAddresses = addressRepository.findByCustomerIdAndIsDeletedFalse(savedCustomer.getId());
        assertEquals(1, customerAddresses.size());
        assertEquals("Pune", customerAddresses.get(0).getCity());
        assertTrue(customerAddresses.get(0).isDefault());
    }
}
