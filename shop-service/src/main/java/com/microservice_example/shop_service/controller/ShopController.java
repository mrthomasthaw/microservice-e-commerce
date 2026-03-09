package com.microservice_example.shop_service.controller;

import com.microservice_example.shop_service.dto.ShopRequestDto;
import com.microservice_example.shop_service.dto.ShopResponseDto;
import com.microservice_example.shop_service.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
@Slf4j
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ShopResponseDto> getShops() throws InterruptedException {
        return shopService.getAllShops();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<ShopResponseDto> getShopById(@PathVariable Long id) {
        return shopService.getShopById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createShop(@RequestBody ShopRequestDto shopRequestDto) {
        log.info("Creating shop: {}", shopRequestDto);
        shopService.createShop(shopRequestDto);
    }

}
