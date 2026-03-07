package com.microservice_example.shop_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopRequestDto {

    private String shopName;

    private String shopAddress;

    private String email;
}
