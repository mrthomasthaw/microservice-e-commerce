package com.microservice_example.order_service.service;

import com.microservice_example.order_service.dto.*;
import com.microservice_example.order_service.model.Order;
import com.microservice_example.order_service.model.OrderItem;
import com.microservice_example.order_service.open_feign_client.CustomerClient;
import com.microservice_example.order_service.open_feign_client.ProductClient;
import com.microservice_example.order_service.open_feign_client.StockClient;
import com.microservice_example.order_service.rabbitmq.producer.RabbitMQProducer;
import com.microservice_example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final StockClient stockClient;

    private final ProductClient productClient;

    private final CustomerClient customerClient;

    private final RabbitMQProducer rabbitMQProducer;

    @Override
    public void createOrder(OrderRequestDto orderRequestDto) {
        List<StockRequestDto> stockRequestDtoList = orderRequestDto.getOrderItems().stream()
                .map(this::mapToStockRequestDto)
                .toList();

        if(! stockClient.isAllStockAvailable(stockRequestDtoList)) {
            throw new IllegalStateException("Out of stock");
        }

        Order order = new Order();
        order.setCustomerId(orderRequestDto.getCustomerId());
        order.setOrderNo(orderRepository.findLastOne()
                .map(this::generateFormCode)
                .orElse("O-0001"));

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

    private void calculateEachItemTotalAmount(OrderItemDto orderItemDto, Map<Long, ProductResponseDto> productResponseDtoMap) {
        ProductResponseDto productResponseDto = productResponseDtoMap.get(orderItemDto.getProductId());
        BigDecimal totalAmount = orderItemDto.getQty().multiply(productResponseDto.getPrice());
        orderItemDto.setTotalAmount(totalAmount);
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        Map<Long, StockResponseDto> stockResponseDtoMap = stockClient.getAllStock()
                .stream()
                .collect(Collectors.toMap(StockResponseDto::getProductId, Function.identity()));

        Map<Long, ProductResponseDto> productResponseDtoMap = productClient.getAllProducts()
                .stream()
                .collect(Collectors.toMap(ProductResponseDto::getId, Function.identity()));

        Map<Long, CustomerResponseDto> customerResponseDtoMap = customerClient.getAllCustomers()
                .stream()
                .collect(Collectors.toMap(CustomerResponseDto::getId, Function.identity()));

        List<OrderResponseDto> orderList = orderRepository.findAll()
                .stream()
                .map(this::mapToOrderResponseDto)
                .toList();

        for(OrderResponseDto orderResponseDto : orderList) {
            setCustomer(orderResponseDto, customerResponseDtoMap);

            for(OrderItemDto orderItemDto : orderResponseDto.getOrderItems()) {
                setOrderItemDtoProduct(orderItemDto, productResponseDtoMap);

                setOrderItemDtoStock(orderItemDto, stockResponseDtoMap);
            }
        }

        return orderList;
    }

    private void setOrderItemDtoStock(OrderItemDto orderItemDto, Map<Long, StockResponseDto> stockResponseDtoMap) {
        StockResponseDto stockResponseDto = stockResponseDtoMap.get(orderItemDto.getProductId());
        orderItemDto.setStockCode(stockResponseDto.getStockCode());
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
        orderResponseDto.setCustomerId(order.getCustomerId());

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

    private StockRequestDto mapToStockRequestDto(OrderItemDto orderItemDto) {
        return StockRequestDto.builder()
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
