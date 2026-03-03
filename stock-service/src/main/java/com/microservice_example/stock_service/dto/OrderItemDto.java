package com.microservice_example.stock_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private Long productId;
    private String productCode;
    private String productName;
    private String stockCode;
    private BigDecimal qty;
    private BigDecimal totalAmount;
}
