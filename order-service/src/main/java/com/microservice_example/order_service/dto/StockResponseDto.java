package com.microservice_example.order_service.dto;

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
    private Long productId;
    private Long shopId;
    private BigDecimal qty;

    public String getProductShopId() {
        return productId + "_" + shopId;
    }
}
