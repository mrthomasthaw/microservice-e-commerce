package com.microservice_example.order_service.service;

import com.microservice_example.order_service.dto.*;
import com.microservice_example.order_service.model.Order;
import com.microservice_example.order_service.model.OrderItem;
import com.microservice_example.order_service.open_feign_client.CustomerClient;
import com.microservice_example.order_service.open_feign_client.ProductClient;
import com.microservice_example.order_service.open_feign_client.ShopClient;
import com.microservice_example.order_service.open_feign_client.StockClient;
import com.microservice_example.order_service.rabbitmq.producer.RabbitMQProducer;
import com.microservice_example.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final StockClient stockClient;

    private final ProductClient productClient;

    private final CustomerClient customerClient;

    private final ShopClient shopClient;

    private final RabbitMQProducer rabbitMQProducer;

    private final ExternalAPIService externalAPIService;

    @CircuitBreaker(name = "stock", fallbackMethod = "onStockServiceFailToResponse")
    //@TimeLimiter(name = "stock")
    @Override
    public void createOrder(OrderRequestDto orderRequestDto) {
        List<StockRequestDto> stockRequestDtoList = orderRequestDto.getOrderItems().stream()
                .map(orderItemDto -> mapToStockRequestDto(orderItemDto, orderRequestDto.getShopId()))
                .toList();


        if(!  stockClient.isAllStockAvailable(stockRequestDtoList)) {
            throw new IllegalStateException("Out of stock");
        }

        Order order = new Order();
        order.setShopId(orderRequestDto.getShopId());
        order.setCustomerId(orderRequestDto.getCustomerId());
        order.setOrderNo(orderRepository.findLastOne()
                .map(this::generateFormCode)
                .orElse("O-0001"));
        order.setOrderDate(LocalDateTime.now());

        Map<Long, ProductResponseDto> productResponseDtoMap = productClient.getAllProducts()
                        .stream()
                        .collect(Collectors.toMap(ProductResponseDto::getId, Function.identity()));


        orderRequestDto.getOrderItems()
                        .forEach(orderItemDto -> {
                            calculateEachItemTotalAmount(orderItemDto, productResponseDtoMap);

                            order.addOrderItem(mapToOrderItem(orderItemDto));
                        });

        orderRepository.save(order);

        rabbitMQProducer.sendMessage(new EventMessage<OrderResponseDto>("order.created", mapToOrderResponseDto(order), LocalDateTime.now()),
                "order.created");
    }


    public void onStockServiceFailToResponse(OrderRequestDto orderRequestDto, RuntimeException e) {
        log.info("Problem with stock service");
        throw e;
    }

    private void calculateEachItemTotalAmount(OrderItemDto orderItemDto, Map<Long, ProductResponseDto> productResponseDtoMap) {
        ProductResponseDto productResponseDto = productResponseDtoMap.get(orderItemDto.getProductId());
        BigDecimal totalAmount = orderItemDto.getQty().multiply(productResponseDto.getPrice());
        orderItemDto.setTotalAmount(totalAmount);
    }

    //@CircuitBreaker(name = "shop", fallbackMethod = "getOrderList")
    @Override
    public List<OrderResponseDto> getAllOrders() {

        CompletableFuture<Map<Long, ShopResponseDto>> shopFutureResponse = externalAPIService.getShopListFutureResponse();

        CompletableFuture<Map<Long, ProductResponseDto>> productFutureResponse = externalAPIService.getProductListFutureResponse();

        CompletableFuture<Map<Long, CustomerResponseDto>> customerFutureResponse = CompletableFuture.supplyAsync(() -> customerClient.getAllCustomers()
                .stream()
                .collect(Collectors.toMap(CustomerResponseDto::getId, Function.identity())));

        CompletableFuture.allOf(productFutureResponse, customerFutureResponse).join();

        Map<Long, ShopResponseDto> shopResponseDtoMap = shopFutureResponse.join();
        Map<Long, ProductResponseDto> productResponseDtoMap = productFutureResponse.join();
        Map<Long, CustomerResponseDto> customerResponseDtoMap = customerFutureResponse.join();

        List<OrderResponseDto> orderList = orderRepository.findAll()
                .stream()
                .map(this::mapToOrderResponseDto)
                .toList();

        for(OrderResponseDto orderResponseDto : orderList) {
            setCustomer(orderResponseDto, customerResponseDtoMap);

            setShop(orderResponseDto, shopResponseDtoMap);

            for(OrderItemDto orderItemDto : orderResponseDto.getOrderItems()) {
                setOrderItemDtoProduct(orderItemDto, productResponseDtoMap);
            }
        }

        return orderList;
    }



    private void setShop(OrderResponseDto orderResponseDto, Map<Long, ShopResponseDto> shopResponseDtoMap) {
        var shopResponseDto = shopResponseDtoMap.get(orderResponseDto.getShopId());
        orderResponseDto.setShopName(shopResponseDto != null ? shopResponseDto.getShopName() : null);
    }

    private List<OrderResponseDto> getOrderList(RuntimeException e) {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToOrderResponseDto)
                .toList();
    }


    private void setOrderItemDtoProduct(OrderItemDto orderItemDto, Map<Long, ProductResponseDto> productResponseDtoMap) {
        ProductResponseDto productResponseDto = productResponseDtoMap.get(orderItemDto.getProductId());

        orderItemDto.setProductCode(productResponseDto.getProductCode());
        orderItemDto.setProductName(productResponseDto.getName());
    }

    private void setCustomer(OrderResponseDto orderResponseDto, Map<Long, CustomerResponseDto> customerResponseDtoMap) {
        CustomerResponseDto customerResponseDto = customerResponseDtoMap.get(orderResponseDto.getCustomerId());
        orderResponseDto.setCustomerName(customerResponseDto.getName());
    }

    private OrderResponseDto mapToOrderResponseDto(Order order) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setId(order.getId());
        orderResponseDto.setOrderNo(order.getOrderNo());
        orderResponseDto.setShopId(order.getShopId());
        orderResponseDto.setCustomerId(order.getCustomerId());
        orderResponseDto.setOrderDate(order.getOrderDate());

        orderResponseDto.setOrderItems(order.getOrderItems()
                .stream()
                .map(this::mapToOrderItemDto)
                .toList());

        return orderResponseDto;
    }

    private OrderItemDto mapToOrderItemDto(OrderItem orderItem) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(orderItem.getProductId());
        orderItemDto.setQty(orderItem.getQty());
        orderItemDto.setTotalAmount(orderItem.getTotalAmount());
        return orderItemDto;
    }

    private StockRequestDto mapToStockRequestDto(OrderItemDto orderItemDto, Long shopId) {
        return StockRequestDto.builder()
                .shopId(shopId)
                .productId(orderItemDto.getProductId())
                .qty(orderItemDto.getQty())
                .build();
    }

    private OrderItem mapToOrderItem(OrderItemDto orderItemDto) {
        return OrderItem.builder()
                .productId(orderItemDto.getProductId())
                .qty(orderItemDto.getQty())
                .totalAmount(orderItemDto.getTotalAmount())
                .build();
    }

    private String generateFormCode(Order order) {
        String code = order.getOrderNo();
        long runningNo = Long.parseLong(code.split("-")[1]);
        return String.format("O-%04d", runningNo + 1);
    }
}
