package com.banking.service;

import com.banking.dto.CustomerRegistrationRequest;
import com.banking.entity.Customer;
import com.banking.entity.CustomerCredential;
import com.banking.exception.DuplicateCustomerException;
import com.banking.repository.CustomerCredentialRepository;
import com.banking.repository.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerCredentialRepository credentialRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRegistrationRequest request;

    @BeforeEach
    void setUp() {
        request = new CustomerRegistrationRequest();
        request.setFirstName("Gowtham");
        request.setLastName("Bandi");
        request.setEmail("customer@example.com");
        request.setMobileNumber("9876543210");
        request.setPassword("SecurePass123");
    }

    @Test
    void shouldRegisterNewCustomerSuccessfully() {
        when(customerRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encoded-password");

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Customer result = customerService.registerCustomer(request);

        assertNotNull(result);
        assertTrue(result.getCustomerNumber().startsWith("CUST"));
        assertEquals("Gowtham", result.getFirstName());
        assertEquals("Bandi", result.getLastName());
        assertEquals("customer@example.com", result.getEmail());
        assertEquals("ACTIVE", result.getStatus());

        ArgumentCaptor<CustomerCredential> captor =
                ArgumentCaptor.forClass(CustomerCredential.class);

        verify(credentialRepository).save(captor.capture());

        assertEquals("encoded-password",
                captor.getValue().getPasswordHash());

        assertEquals("ACTIVE", captor.getValue().getStatus());
        verify(passwordEncoder).encode("SecurePass123");
    }

    @Test
    void shouldRejectDuplicateEmail() {
        when(customerRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(
                DuplicateCustomerException.class,
                () -> customerService.registerCustomer(request)
        );

        verify(customerRepository, never()).save(any(Customer.class));
        verify(credentialRepository, never())
                .save(any(CustomerCredential.class));
        verifyNoInteractions(passwordEncoder);
    }
}
