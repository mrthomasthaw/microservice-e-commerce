package com.microservice_example.shop_service.service;

import com.microservice_example.shop_service.dto.ShopRequestDto;
import com.microservice_example.shop_service.dto.ShopResponseDto;

import java.util.List;
import java.util.Optional;

public interface ShopService {

    void createShop(ShopRequestDto shopRequestDto);

    List<ShopResponseDto> getAllShops() throws InterruptedException;

    Optional<ShopResponseDto> getShopById(Long id);
}
