package com.microservice_example.order_service.repository;

import com.microservice_example.order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("Select o From Order o Order By o.id Desc Limit 1")
    Optional<Order> findLastOne();
}
