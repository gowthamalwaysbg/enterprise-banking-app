package com.banking.repository;

import com.banking.entity.CustomerCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerCredentialRepository
        extends JpaRepository<CustomerCredential, Long> {

    Optional<CustomerCredential> findByCustomer_CustomerNumber(
            String customerNumber
    );

    boolean existsByCustomer_Id(Long customerId);
}
