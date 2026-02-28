package com.microservice_example.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
