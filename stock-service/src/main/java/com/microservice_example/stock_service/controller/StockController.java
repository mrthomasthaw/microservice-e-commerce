package com.microservice_example.stock_service.controller;

import com.microservice_example.stock_service.dto.EventMessage;
import com.microservice_example.stock_service.dto.StockRequestDto;
import com.microservice_example.stock_service.dto.StockResponseDto;
import com.microservice_example.stock_service.rabbitmq.producer.RabbitMQProducer;
import com.microservice_example.stock_service.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Slf4j
public class StockController {

    private final StockService stockService;

    private final RabbitMQProducer rabbitMQProducer;

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
    public boolean isAllStockAvailable(@RequestBody List<StockRequestDto> dtoList) throws InterruptedException {
        log.info("stock available start wait");
        Thread.sleep(30000);
        log.info("stock available end wait");
        return stockService.isAllStockAvailable(dtoList);
    }

    @GetMapping("/publish")
    public String sendMessage(@RequestParam("message") String message) {
        rabbitMQProducer.sendMessage(new EventMessage<String>("stock.update-failed", null, LocalDateTime.now()),
                "stock.update-failed");
        return "Message send";
    }
}
