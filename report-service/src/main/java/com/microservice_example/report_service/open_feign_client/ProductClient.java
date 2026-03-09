package com.microservice_example.report_service.open_feign_client;

import com.microservice_example.report_service.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/products")
    List<ProductResponseDto> getAllProducts();
}
