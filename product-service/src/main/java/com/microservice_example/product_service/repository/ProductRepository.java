package com.microservice_example.product_service.repository;


import com.microservice_example.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    @Query("Select p From Product p Order By p.id Desc Limit 1")
    Optional<Product> findLastOne();
}
