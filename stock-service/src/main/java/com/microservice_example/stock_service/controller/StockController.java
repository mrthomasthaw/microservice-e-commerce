package com.microservice_example.stock_service.controller;

import com.microservice_example.stock_service.dto.StockRequestDto;
import com.microservice_example.stock_service.dto.StockResponseDto;
import com.microservice_example.stock_service.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createStock(@RequestBody StockRequestDto stockRequestDto) {
        stockService.createStock(stockRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<StockResponseDto> getAllStocks() {
        return stockService.getAllStocks();
    }


    @PostMapping("/check-stock")
    @ResponseStatus(HttpStatus.OK)
    public boolean isAllStockAvailable(@RequestBody List<StockRequestDto> dtoList) {
        return stockService.isAllStockAvailable(dtoList);
    }
}
