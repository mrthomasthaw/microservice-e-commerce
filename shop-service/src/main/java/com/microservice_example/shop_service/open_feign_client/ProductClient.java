package com.microservice_example.shop_service.open_feign_client;

import com.microservice_example.shop_service.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@FeignClient(name = "product-service")
public interface ProductClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/product")
    List<ProductResponseDto> getAllProducts();
}
