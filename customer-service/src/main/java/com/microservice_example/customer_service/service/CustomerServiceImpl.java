package com.microservice_example.customer_service.service;

import com.microservice_example.customer_service.dto.CustomerRequestDto;
import com.microservice_example.customer_service.dto.CustomerResponseDto;
import com.microservice_example.customer_service.model.Customer;
import com.microservice_example.customer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;


    @Override
    public void createCustomer(CustomerRequestDto customerRequestDto) {
        Customer customer = Customer.builder()
                        .name(customerRequestDto.getName())
                        .address(customerRequestDto.getAddress())
                        .build();

        customerRepository.save(customer);
    }

    @Override
    public List<CustomerResponseDto> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public Optional<CustomerResponseDto> getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::mapToResponseDto);
    }

    private CustomerResponseDto mapToResponseDto(Customer customer) {
        return CustomerResponseDto
                .builder()
                .id(customer.getId())
                .address(customer.getAddress())
                .name(customer.getName())
                .build();
    }
}
