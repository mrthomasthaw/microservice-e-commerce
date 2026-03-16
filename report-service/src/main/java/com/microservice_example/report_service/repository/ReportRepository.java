package com.microservice_example.report_service.repository;

import com.microservice_example.report_service.model.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface ReportRepository extends JpaRepository<OrderHistory, Long> {
    @Query(value = """
        Select oh.product_code, oh.product_name, SUM(oh.qty) 
        From t_order_history oh 
        Where (:fromDate is null Or DATE(oh.order_date) >= :fromDate)
        And (:toDate is null Or DATE(oh.order_date) <= :toDate)
        Group By oh.product_code, oh.product_name
        Order By SUM(oh.qty) Desc
        """, nativeQuery = true)
    List<Object[]> findOrderHistoryByMostSellingProduct(@Param("fromDate")LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query(value = """
        Select oh.shop_name, oh.shop_email, oh.shop_address, SUM(oh.qty) 
        From t_order_history oh
        Where (:fromDate is null Or DATE(oh.order_date) >= :fromDate)
        And (:toDate is null Or DATE(oh.order_date) <= :toDate)
        Group By oh.shop_name, oh.shop_address, oh.shop_email
        Order By SUM(oh.qty) Desc
        """, nativeQuery = true)
    List<Object[]> findOrderHistoryByMostSellingShop(@Param("fromDate")LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query(value = """
        Select DATE (oh.order_date), SUM(oh.qty), COUNT(oh.product_code) 
        From t_order_history oh
        Where (:fromDate is null Or DATE(oh.order_date) >= :fromDate)
        And (:toDate is null Or DATE(oh.order_date) <= :toDate)
        Group By DAte(oh.order_date)
        Order By SUM(oh.qty) Desc
        """, nativeQuery = true)
    List<Object[]> findOrderHistoryByDaily(@Param("fromDate")LocalDate fromDate, @Param("toDate") LocalDate toDate);


    @Query(value = """
        Select oh.order_no, oh.product_code, oh.product_name, oh.qty, DATE(oh.order_date), oh.shop_name, oh.shop_address 
        From t_order_history oh
        Where (:fromDate is null Or DATE(oh.order_date) >= :fromDate)
        And (:toDate is null Or DATE(oh.order_date) <= :toDate)
        And (:customerName is null Or :customerName = '' Or oh.customer_name = :customerName)
        And (:productCode is null Or :productCode = '' Or oh.product_code = :productCode)
        And (:shopName is null Or :shopName = '' Or oh.shop_name = :shopName)
        Order By oh.order_no Desc
        """, nativeQuery = true)
    List<Object[]> findOrderHistoryByDetailFilter(@Param("customerName") String customerName, @Param("productCode") String productCode,
                                           @Param("shopName") String shopName, @Param("fromDate")LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query(value = """
        Select oh.id, oh.order_no, oh.product_code, oh.product_name, oh.qty, DATE(oh.order_date), oh.shop_name, oh.shop_address
        From t_order_history oh
        Where (:fromDate is null Or DATE(oh.order_date) >= :fromDate)
        And (:toDate is null Or DATE(oh.order_date) <= :toDate)
        And (:customerName is null Or :customerName = '' Or oh.customer_name = :customerName)
        And (:productCode is null Or :productCode = '' Or oh.product_code = :productCode)
        And (:shopName is null Or :shopName = '' Or oh.shop_name = :shopName)
        And (:lastId is null Or oh.Id < :lastId)
        Order By oh.order_no Desc
        Limit :limit
        """, nativeQuery = true)
    List<Object[]> findOrderHistoryByDetailFilter(@Param("customerName") String customerName, @Param("productCode") String productCode,
                                                  @Param("shopName") String shopName, @Param("fromDate")LocalDate fromDate, @Param("toDate") LocalDate toDate,
                                                  @Param("limit") Integer limit, @Param("lastId") Long lastId);


    @Query(value = """
        Select oh.order_no, oh.product_code, oh.product_name, oh.qty, DATE(oh.order_date), oh.shop_name, oh.shop_address 
        From t_order_history oh
        Where (:fromDate is null Or DATE(oh.order_date) >= :fromDate)
        And (:toDate is null Or DATE(oh.order_date) <= :toDate)
        And (:customerName is null Or :customerName = '' Or oh.customer_name = :customerName)
        And (:productCode is null Or :productCode = '' Or oh.product_code = :productCode)
        And (:shopName is null Or :shopName = '' Or oh.shop_name = :shopName)
        Order By oh.order_no Desc
        """, nativeQuery = true)
    Stream<Object[]> findOrderHistoryStreamByDetailFilter(@Param("customerName") String customerName, @Param("productCode") String productCode,
                                                    @Param("shopName") String shopName, @Param("fromDate")LocalDate fromDate, @Param("toDate") LocalDate toDate);


}
