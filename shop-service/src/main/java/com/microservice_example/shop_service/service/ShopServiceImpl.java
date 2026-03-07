package com.microservice_example.shop_service.service;

import com.microservice_example.shop_service.dto.ProductResponseDto;
import com.microservice_example.shop_service.dto.ShopResponseDto;
import com.microservice_example.shop_service.model.Shop;
import com.microservice_example.shop_service.open_feign_client.ProductClient;
import com.microservice_example.shop_service.repository.ShopRepository;
import com.microservice_example.shop_service.dto.ShopRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;

    private final ProductClient productClient;

    @Override
    public void createShop(ShopRequestDto shopRequestDto) {
        Shop shop = Shop.builder()
                        .email(shopRequestDto.getEmail())
                        .name(shopRequestDto.getShopName())
                        .address(shopRequestDto.getShopAddress())
                        .build();

        shopRepository.save(shop);

        log.info("Shop created id {} ", shop.getId());
    }

    @Override
    public List<ShopResponseDto> getAllShops() {
        Map<Long, List<ProductResponseDto>> productResponseMap = productClient.getAllProducts()
                .stream()
                .filter(product -> product.getShopId() != null)
                .collect(Collectors.groupingBy(ProductResponseDto::getShopId));

        return shopRepository.findAll().stream()
                .map(shop -> {
                    ShopResponseDto shopResponseDto = new ShopResponseDto();
                    shopResponseDto.setId(shop.getId());
                    shopResponseDto.setShopName(shop.getName());
                    shopResponseDto.setShopAddress(shop.getAddress());
                    shopResponseDto.setEmail(shop.getEmail());
                    shopResponseDto.setProducts(productResponseMap.get(shop.getId()));
                    return shopResponseDto;
                })
                .toList();
    }

    @Override
    public Optional<ShopResponseDto> getShopById(Long id) {
        return shopRepository.findById(id)
                .map(this::mapToShopResponseDto);
    }

    private ShopResponseDto mapToShopResponseDto(Shop shop) {
        return ShopResponseDto
                .builder()
                .id(shop.getId())
                .shopAddress(shop.getAddress())
                .email(shop.getEmail())
                .shopName(shop.getName())
                .build();
    }
}
