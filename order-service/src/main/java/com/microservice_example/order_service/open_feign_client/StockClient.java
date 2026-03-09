package com.microservice_example.order_service.open_feign_client;

import com.microservice_example.order_service.dto.StockRequestDto;
import com.microservice_example.order_service.dto.StockResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "stock-service")
public interface StockClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/stocks")
    List<StockResponseDto> getAllStock();

    @RequestMapping(method = RequestMethod.POST, value = "/api/stocks/check-stock")
    boolean isAllStockAvailable(@RequestBody List<StockRequestDto> stockRequestDtoList);

}
