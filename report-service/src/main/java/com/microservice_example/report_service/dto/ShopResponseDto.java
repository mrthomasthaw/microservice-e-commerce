package com.microservice_example.report_service.dto;

import lombok.Data;

@Data
public class ShopResponseDto {

    private Long id;
    private String shopName;
    private String shopAddress;
    private String email;
}
