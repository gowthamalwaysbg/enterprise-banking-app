package com.banking.security;

import com.banking.entity.Customer;
import com.banking.entity.CustomerCredential;
import com.banking.repository.CustomerCredentialRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final CustomerCredentialRepository credentialRepository;

    public CustomerUserDetailsService(
            CustomerCredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String customerNumber)
            throws UsernameNotFoundException {

        CustomerCredential credential = credentialRepository
                .findByCustomer_CustomerNumber(customerNumber)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Invalid customer number or password"
                ));

        Customer customer = credential.getCustomer();

        boolean active = "ACTIVE".equalsIgnoreCase(customer.getStatus())
                && "ACTIVE".equalsIgnoreCase(credential.getStatus());

        return User.withUsername(customer.getCustomerNumber())
                .password(credential.getPasswordHash())
                .authorities("ROLE_CUSTOMER")
                .disabled(!active)
                .build();
    }
}
