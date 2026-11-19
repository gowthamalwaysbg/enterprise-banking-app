package com.banking.service;

import com.banking.dto.CustomerRegistrationRequest;
import com.banking.entity.Customer;
import com.banking.entity.CustomerCredential;
import com.banking.exception.DuplicateCustomerException;
import com.banking.repository.CustomerRepository;
import com.banking.repository.CustomerCredentialRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerCredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(
            CustomerRepository customerRepository,
            CustomerCredentialRepository credentialRepository,
            PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Customer registerCustomer(CustomerRegistrationRequest request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateCustomerException(
                "A customer with this email already exists"
            );
        }

        Customer customer = new Customer();

        customer.setCustomerNumber(generateCustomerNumber());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setStatus("ACTIVE");

        Customer savedCustomer = customerRepository.save(customer);

        CustomerCredential credential = new CustomerCredential();
        credential.setCustomer(savedCustomer);
        credential.setPasswordHash(
            passwordEncoder.encode(request.getPassword())
        );
        credential.setStatus("ACTIVE");

        credentialRepository.save(credential);

        return savedCustomer;
    }

    private String generateCustomerNumber() {
        return "CUST" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}
