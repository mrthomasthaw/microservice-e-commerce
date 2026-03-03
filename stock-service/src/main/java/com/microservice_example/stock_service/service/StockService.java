package com.microservice_example.stock_service.service;

import com.microservice_example.stock_service.dto.OrderResponseDto;
import com.microservice_example.stock_service.dto.StockRequestDto;
import com.microservice_example.stock_service.dto.StockResponseDto;

import java.util.List;

public interface StockService {
    List<StockResponseDto> getAllStocks();

    void createStock(StockRequestDto stockRequestDto);

    boolean isAllStockAvailable(List<StockRequestDto> stockRequestDtoList);

    void updateStockQty(OrderResponseDto orderResponseDto);
}
