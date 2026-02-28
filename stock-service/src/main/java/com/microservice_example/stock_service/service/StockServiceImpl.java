package com.microservice_example.stock_service.service;

import com.microservice_example.stock_service.dto.StockRequestDto;
import com.microservice_example.stock_service.dto.StockResponseDto;
import com.microservice_example.stock_service.model.Stock;
import com.microservice_example.stock_service.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;


    @Override
    public List<StockResponseDto> getAllStocks() {
        return stockRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public void createStock(StockRequestDto stockRequestDto) {
        Stock stock = Stock.builder()
                .stockCode(stockRepository.findLastOne()
                        .map(this::generateFormCode)
                        .orElse("ST-0001"))
                .productId(stockRequestDto.getProductId())
                .qty(stockRequestDto.getQty())
                .build();

        stockRepository.save(stock);
    }

    @Override
    public boolean isAllStockAvailable(List<StockRequestDto> stockRequestDtoList) {
        List<Long> productIdList = stockRequestDtoList.stream()
                .map(StockRequestDto::getProductId)
                .toList();

        Map<Long, Stock> stockMap = stockRepository.findAllByProductIdIn(productIdList)
                .stream()
                .collect(Collectors.toMap(Stock::getProductId, Function.identity()));

        int availableItems = 0;
        for(var stockRequestDto : stockRequestDtoList) {
            var stock = stockMap.get(stockRequestDto.getProductId());

            if(stockRequestDto.getQty().compareTo(stock.getQty()) <= 0)
                availableItems++;
        }


        return availableItems == stockRequestDtoList.size();
    }

    public StockResponseDto mapToResponseDto(Stock stock) {
        return StockResponseDto.builder()
                .stockCode(stock.getStockCode())
                .qty(stock.getQty())
                .productId(stock.getProductId())
                .build();
    }

    private String generateFormCode(Stock lastStock) {
        String code = lastStock.getStockCode();
        long runningNo = Long.parseLong(code.split("-")[1]);
        return String.format("ST-%04d", runningNo + 1);
    }
}
