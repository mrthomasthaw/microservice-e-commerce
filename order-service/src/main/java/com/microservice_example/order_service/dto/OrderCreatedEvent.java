package com.microservice_example.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderCreatedEvent {
    private OrderResponseDto orderResponseDto;
    private LocalDateTime timestamp;
}
