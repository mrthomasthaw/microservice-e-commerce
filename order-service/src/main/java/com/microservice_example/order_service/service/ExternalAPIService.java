package com.microservice_example.order_service.service;

import com.microservice_example.order_service.dto.CustomerResponseDto;
import com.microservice_example.order_service.dto.ProductResponseDto;
import com.microservice_example.order_service.dto.ShopResponseDto;
import com.microservice_example.order_service.dto.StockRequestDto;
import com.microservice_example.order_service.open_feign_client.CustomerClient;
import com.microservice_example.order_service.open_feign_client.ProductClient;
import com.microservice_example.order_service.open_feign_client.ShopClient;
import com.microservice_example.order_service.open_feign_client.StockClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExternalAPIService {

    private final StockClient stockClient;

    private final ProductClient productClient;

    private final CustomerClient customerClient;

    private final ShopClient shopClient;

    @CircuitBreaker(name = "shop", fallbackMethod = "getEmptyShopList")
    @TimeLimiter(name = "shop")
    @Bulkhead(name = "shop", type = Bulkhead.Type.THREADPOOL)
    public @NotNull CompletableFuture<Map<Long, ShopResponseDto>> getShopListFutureResponse() {
        return CompletableFuture.supplyAsync(() -> shopClient.getAllShops()
                .stream()
                .collect(Collectors.toMap(ShopResponseDto::getId, Function.identity())));
    }

    private CompletableFuture<Map<Long, ShopResponseDto>> getEmptyShopList(Throwable throwable) {
        return CompletableFuture.completedFuture(Collections.emptyMap());
    }

    @CircuitBreaker(name = "product", fallbackMethod = "getEmptyProductList")
    @TimeLimiter(name = "product")
    public @NotNull CompletableFuture<Map<Long, ProductResponseDto>> getProductListFutureResponse() {
        return CompletableFuture.supplyAsync(() -> productClient.getAllProducts()
                .stream()
                .collect(Collectors.toMap(ProductResponseDto::getId, Function.identity())));
    }

    private CompletableFuture<Map<Long, ProductResponseDto>> getEmptyProductList(Throwable throwable) {
        return CompletableFuture.completedFuture(Collections.emptyMap());
    }

    @CircuitBreaker(name = "customer", fallbackMethod = "getEmptyCustomerList")
    @TimeLimiter(name = "customer")
    public @NotNull CompletableFuture<Map<Long, CustomerResponseDto>> getCustomerListFutureResponse() {
        return CompletableFuture.supplyAsync(() -> customerClient.getAllCustomers()
                .stream()
                .collect(Collectors.toMap(CustomerResponseDto::getId, Function.identity())));
    }

    private CompletableFuture<Map<Long, ProductResponseDto>> getEmptyCustomerList(Throwable throwable) {
        return CompletableFuture.completedFuture(Collections.emptyMap());
    }

    @CircuitBreaker(name = "stock")
    @TimeLimiter(name = "stock")
    @Retry(name = "stock")
    public @NotNull CompletableFuture<Boolean> checkAllStockAvailableFutureResponse(List<StockRequestDto> stockRequestDtoList) {
        return CompletableFuture.supplyAsync(() -> stockClient.isAllStockAvailable(stockRequestDtoList));
    }

//    private CompletableFuture<Boolean> checkAllStockAvailableFailRemoteCall(RuntimeException e) {
//        return CompletableFuture.completedFuture(() -> ;throw new RuntimeException(""));
//    }
}
