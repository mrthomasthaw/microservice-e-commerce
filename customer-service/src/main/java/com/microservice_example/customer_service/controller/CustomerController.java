package com.microservice_example.customer_service.controller;

import com.microservice_example.customer_service.dto.CustomerRequestDto;
import com.microservice_example.customer_service.dto.CustomerResponseDto;
import com.microservice_example.customer_service.model.Customer;
import com.microservice_example.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerResponseDto> getAllCustomers() throws InterruptedException {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<CustomerResponseDto> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createCustomer(@RequestBody CustomerRequestDto customerRequestDto) {
        customerService.createCustomer(customerRequestDto);
    }
}
