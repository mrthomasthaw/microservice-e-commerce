package com.microservice_example.stock_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;
    private String orderNo;
    private Long customerId;
    private String customerName;
    private List<OrderItemDto> orderItems = new ArrayList<>();
}
