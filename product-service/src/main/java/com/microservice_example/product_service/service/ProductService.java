package com.microservice_example.product_service.service;


import com.microservice_example.product_service.dto.ProductRequestDto;
import com.microservice_example.product_service.dto.ProductResponseDto;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    void createProduct(ProductRequestDto productRequestDto);

    List<ProductResponseDto> getAllProducts();

    Optional<ProductResponseDto> getById(Long id);
}
