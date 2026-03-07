package com.microservice_example.shop_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopResponseDto {

    private Long id;

    private String shopName;

    private String shopAddress;

    private String email;

    private List<ProductResponseDto> products;
}
