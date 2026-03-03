package com.microservice_example.stock_service.repository;

import com.microservice_example.stock_service.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("Select s From Stock s Order By s.id Desc Limit 1")
    Optional<Stock> findLastOne();

    List<Stock> findAllByProductIdIn(List<Long> productIdList);

    List<Stock> findAllByStockCodeIn(List<String> stockCodeList);
}
