package com.microservice_example.customer_service.dto;

import lombok.Data;

@Data
public class CustomerRequestDto {

    private String name;
    private String address;
}
