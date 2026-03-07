package com.microservice_example.shop_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopProductDto {

    private Long id;

    private String name;

    private String address;
}
