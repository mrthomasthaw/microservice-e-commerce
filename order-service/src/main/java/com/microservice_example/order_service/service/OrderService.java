package com.microservice_example.order_service.service;

import com.microservice_example.order_service.dto.OrderRequestDto;
import com.microservice_example.order_service.dto.OrderResponseDto;

import java.util.List;

public interface OrderService {
    void createOrder(OrderRequestDto orderRequestDto);

    List<OrderResponseDto> getAllOrders();
}
