package com.microservice_example.stock_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockResponseDto {

    private Long id;
    private String stockCode;
    private Long shopId;
    private Long productId;
    private BigDecimal qty;
}
