package com.microservice_example.order_service.model;

import com.microservice_example.order_service.dto.OrderItemDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "t_order_item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private BigDecimal qty;

    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

}
