package com.microservice_example.report_service.dto;

import lombok.Data;

@Data
public class CustomerResponseDto {
    private Long id;
    private String name;
    private String address;
}
