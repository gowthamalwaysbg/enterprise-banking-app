package com.banking.controller;

import com.banking.dto.CustomerRegistrationRequest;
import com.banking.entity.Customer;
import com.banking.repository.CustomerRepository;
import com.banking.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    public CustomerController(
            CustomerService customerService,
            CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(
            @Valid @RequestBody CustomerRegistrationRequest request) {

        Customer customer = customerService.registerCustomer(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "message", "Customer registered successfully",
                "customerNumber", customer.getCustomerNumber(),
                "firstName", customer.getFirstName(),
                "lastName", customer.getLastName(),
                "email", customer.getEmail(),
                "status", customer.getStatus()
            )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentCustomer(Authentication authentication) {

        Customer customer = customerRepository
                .findByCustomerNumber(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found"));

        return ResponseEntity.ok(
            Map.of(
                "customerNumber", customer.getCustomerNumber(),
                "firstName", customer.getFirstName(),
                "lastName", customer.getLastName(),
                "email", customer.getEmail(),
                "mobileNumber", customer.getMobileNumber(),
                "status", customer.getStatus()
            )
        );
    }
}
