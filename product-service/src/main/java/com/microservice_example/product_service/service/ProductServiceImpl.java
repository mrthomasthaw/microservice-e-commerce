package com.microservice_example.product_service.service;

import com.microservice_example.product_service.dto.ProductRequestDto;
import com.microservice_example.product_service.dto.ProductResponseDto;
import com.microservice_example.product_service.model.Product;
import com.microservice_example.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public void createProduct(ProductRequestDto productRequestDto) {
        Product product = Product.builder()
                .productCode(productRepository.findLastOne()
                        .map(this::generateFormCode)
                        .orElse("P-0001"))
                .name(productRequestDto.getName())
                .description(productRequestDto.getDescription())
                .price(productRequestDto.getPrice())
                .shopId(productRequestDto.getShopId())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    private String generateFormCode(Product product) {
        String code = product.getProductCode();
        long runningNo = Long.parseLong(code.split("-")[1]);
        return String.format("P-%04d", runningNo + 1);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() throws InterruptedException {
//        log.info("start waiting for products");
//        Thread.sleep(100000);
//        log.info("end waiting for products");

        return productRepository.findAll()
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Override
    public Optional<ProductResponseDto> getById(Long id) {
        return productRepository.findById(id).map(this::mapToProductResponse);
    }

    public ProductResponseDto mapToProductResponse(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .productCode(product.getProductCode())
                .description(product.getDescription())
                .price(product.getPrice())
                .shopId(product.getShopId())
                .build();
    }
}
