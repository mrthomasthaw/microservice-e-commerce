package com.microservice_example.shop_service.repository;

import com.microservice_example.shop_service.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}
