package com.banking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CustomerLoginRequest {

    @NotBlank(message = "Customer number is required")
    @Size(max = 20)
    private String customerNumber;

    @NotBlank(message = "Password is required")
    @Size(max = 72)
    private String password;

    public CustomerLoginRequest() {}

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
