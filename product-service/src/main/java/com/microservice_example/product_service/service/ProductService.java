package com.microservice_example.product_service.service;


import com.microservice_example.product_service.dto.ProductRequestDto;
import com.microservice_example.product_service.dto.ProductResponseDto;

import java.util.List;

public interface ProductService {
    void createProduct(ProductRequestDto productRequestDto);

    List<ProductResponseDto> getAllProducts();
}
