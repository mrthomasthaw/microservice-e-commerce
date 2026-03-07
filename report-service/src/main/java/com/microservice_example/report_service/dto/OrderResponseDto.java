package com.microservice_example.report_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;
    private String orderNo;
    private Long shopId;
    private Long customerId;
    private String customerName;
    private String shopName;
    private LocalDateTime orderDate;
    private List<OrderItemDto> orderItems = new ArrayList<>();
}
