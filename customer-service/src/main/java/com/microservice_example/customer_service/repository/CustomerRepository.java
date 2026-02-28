package com.microservice_example.customer_service.repository;

import com.microservice_example.customer_service.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
