package com.microservice_example.customer_service.service;

import com.microservice_example.customer_service.dto.CustomerRequestDto;
import com.microservice_example.customer_service.dto.CustomerResponseDto;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    void createCustomer(CustomerRequestDto customerRequestDto);

    List<CustomerResponseDto> getAllCustomers();

    Optional<CustomerResponseDto> getCustomerById(Long id);
}
