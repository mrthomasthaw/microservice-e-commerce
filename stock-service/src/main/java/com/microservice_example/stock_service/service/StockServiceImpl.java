package com.microservice_example.stock_service.service;

import com.microservice_example.stock_service.dto.OrderItemDto;
import com.microservice_example.stock_service.dto.OrderResponseDto;
import com.microservice_example.stock_service.dto.StockRequestDto;
import com.microservice_example.stock_service.dto.StockResponseDto;
import com.microservice_example.stock_service.model.Stock;
import com.microservice_example.stock_service.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
//                .stockCode(stockRepository.findLastOne()
//                        .map(this::generateFormCode)
//                        .orElse("ST-0001"))
                .shopId(stockRequestDto.getShopId())
                .productId(stockRequestDto.getProductId())
                .qty(stockRequestDto.getQty())
                .build();

        stockRepository.save(stock);
    }

    @Override
    public boolean isAllStockAvailable(List<StockRequestDto> stockRequestDtoList) throws InterruptedException {
        List<Long> productIdList = stockRequestDtoList.stream()
                .map(StockRequestDto::getProductId)
                .toList();

        Long shopId = stockRequestDtoList.get(0).getShopId();

        Map<Long, Stock> stockMap = stockRepository.findAllByShopIdAndProductIdIn(shopId, productIdList)
                .stream()
                .collect(Collectors.toMap(Stock::getProductId, Function.identity()));

        int availableItems = 0;
        for(var stockRequestDto : stockRequestDtoList) {
            var stock = stockMap.get(stockRequestDto.getProductId());

            if(stock == null)
                throw new RuntimeException("Stock not found in this shop Id : " + stockRequestDto.getShopId());

            if(stockRequestDto.getQty().compareTo(stock.getQty()) <= 0)
                availableItems++;
        }


        return availableItems == stockRequestDtoList.size();
    }

    @Override
    public void updateStockQty(OrderResponseDto orderResponseDto) {
        Map<Long, OrderItemDto> orderItemDtoMap = orderResponseDto.getOrderItems()
                .stream()
                .collect(Collectors.toMap(OrderItemDto::getProductId, Function.identity()));

        Long shopId = orderResponseDto.getShopId();
        List<Stock> stocks = new ArrayList<>();
        stockRepository.findAllByShopIdAndProductIdIn(shopId, orderItemDtoMap.keySet().stream().toList())
                        .forEach(stock -> {
                            BigDecimal deductQty = orderItemDtoMap.get(stock.getProductId()).getQty();
                            stock.setQty(stock.getQty().subtract(deductQty));
                            stocks.add(stock);
                        });

        stockRepository.saveAll(stocks);
    }

    public StockResponseDto mapToResponseDto(Stock stock) {
        return StockResponseDto.builder()
                .id(stock.getId())
                //.stockCode(stock.getStockCode())
                .qty(stock.getQty())
                .shopId(stock.getShopId())
                .productId(stock.getProductId())
                .build();
    }

    private String generateFormCode(Stock lastStock) {
        //String code = lastStock.getStockCode();
        String code = "";
        long runningNo = Long.parseLong(code.split("-")[1]);
        return String.format("ST-%04d", runningNo + 1);
    }
}
