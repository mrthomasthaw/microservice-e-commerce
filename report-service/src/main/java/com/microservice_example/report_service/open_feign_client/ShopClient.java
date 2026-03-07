package com.microservice_example.report_service.open_feign_client;

import com.microservice_example.report_service.dto.ShopResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "shop-service")
public interface ShopClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/shop")
    List<ShopResponseDto> getAllShops();

    @RequestMapping(method = RequestMethod.GET, value = "/api/shop/{id}")
    Optional<ShopResponseDto> getShopById(@PathVariable("id") Long id);

}
