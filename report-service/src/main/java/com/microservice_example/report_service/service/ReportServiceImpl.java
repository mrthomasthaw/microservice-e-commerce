package com.microservice_example.report_service.service;

import com.microservice_example.report_service.dto.*;
import com.microservice_example.report_service.model.OrderHistory;
import com.microservice_example.report_service.open_feign_client.CustomerClient;
import com.microservice_example.report_service.open_feign_client.ProductClient;
import com.microservice_example.report_service.open_feign_client.ShopClient;
import com.microservice_example.report_service.repository.ReportRepository;
import com.microservice_example.report_service.util.CommonExcelUtil;
import com.microservice_example.report_service.util.ReportType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    private final ShopClient shopClient;

    private final CustomerClient customerClient;

    private final ProductClient productClient;

    private final EntityManager entityManager;

    @Override
    public void createOrderHistory(OrderResponseDto orderResponseDto) {
        List<OrderHistory> orderHistoryList = new ArrayList<>();
        Optional<ShopResponseDto> shopResponseDto = shopClient.getShopById(orderResponseDto.getShopId());

        Optional<CustomerResponseDto> customerResponseDto = customerClient.getCustomerById(orderResponseDto.getCustomerId());

        Map<Long, ProductResponseDto> productResponseDtoMap = productClient.getAllProducts()
                        .stream()
                        .collect(Collectors.toMap(ProductResponseDto::getId, Function.identity()));

        orderResponseDto.getOrderItems()
                .forEach(orderItemDto -> {
                    var orderHistory = new OrderHistory();
                    orderHistory.setOrderDate(orderResponseDto.getOrderDate());
                    orderHistory.setOrderNo(orderResponseDto.getOrderNo());

                    var shopResponse = shopResponseDto.orElseThrow();
                    orderHistory.setShopName(shopResponse.getShopName());
                    orderHistory.setShopEmail(shopResponse.getEmail());
                    orderHistory.setShopAddress(shopResponse.getShopAddress());
                    orderHistory.setCustomerName(customerResponseDto.map(CustomerResponseDto::getName).orElseThrow(() -> new RuntimeException("Customer not found")));

                    var productResponse = productResponseDtoMap.get(orderItemDto.getProductId());
                    orderHistory.setProductCode(productResponse.getProductCode());
                    orderHistory.setProductName(productResponse.getName());
                    orderHistory.setQty(orderItemDto.getQty());

                    orderHistoryList.add(orderHistory);
                });

        reportRepository.saveAll(orderHistoryList);
    }

    @Override
    public void exportExcel(ReportRequestDto reportRequestDto, OutputStream outputStream) throws IOException {
        var reportType = ReportType.getByCode(reportRequestDto.getReportType())
                .orElseThrow();

        switch (reportType) {
            case ReportType.PRODUCT_SUMMARY -> {
                List<Object[]> list = reportRepository.findOrderHistoryByMostSellingProduct(reportRequestDto.getFromOrderDate(), reportRequestDto.getToOrderDate());
                CommonExcelUtil.writeToExcel(List.of(
                        "No",
                        "Product Code",
                        "Product Name",
                        "Qty"
                ), list, outputStream);
            }

            case ReportType.SHOP_SUMMARY -> {
                List<Object[]> list = reportRepository.findOrderHistoryByMostSellingShop(reportRequestDto.getFromOrderDate(), reportRequestDto.getToOrderDate());
                CommonExcelUtil.writeToExcel(List.of(
                        "No",
                        "Shop Name",
                        "Email",
                        "Address",
                        "Qty"
                ), list, outputStream);
            }

            case ReportType.DAILY_SALE_SUMMARY -> {
                List<Object[]> list = reportRepository.findOrderHistoryByDaily(reportRequestDto.getFromOrderDate(), reportRequestDto.getToOrderDate());
                CommonExcelUtil.writeToExcel(List.of(
                        "No",
                        "Date",
                        "Qty",
                        "Total Product"
                ), list, outputStream);
            }

            case ReportType.DETAIL_REPORT -> {
                List<Object[]> list = reportRepository.findOrderHistoryByDetailFilter(
                        reportRequestDto.getCustomerName(), reportRequestDto.getProductCode(),
                        reportRequestDto.getShopName(), reportRequestDto.getFromOrderDate(), reportRequestDto.getToOrderDate());

                CommonExcelUtil.writeToExcel(List.of(
                        "No",
                        "Order No",
                        "Product Code",
                        "Product Name",
                        "Qty",
                        "Order Date",
                        "Shop",
                        "Shop Address"
                ), list, outputStream);
            }
        }
    }

    @Transactional
    public void generateDataset(int totalRows) {

        int batchSize = 5000;

        for (int i = 1; i <= totalRows; i++) {
            System.out.println("saving " + i + " rows");
            OrderHistory oh = new OrderHistory();

            oh.setCreatedTime(LocalDateTime.now());
            oh.setCustomerName("cust " + (i % 100));
            oh.setOrderDate(LocalDateTime.of(2026,1,1,0,0).plusDays(i % 120));
            oh.setOrderNo(String.format("O-%07d", i / 3));
            oh.setProductCode(String.format("P-%04d", (i % 200) + 1));
            oh.setProductName("product " + (i % 200));
            oh.setQty(BigDecimal.valueOf((int)(Math.random() * 50) + 1));
            oh.setShopName("shop " + (i % 100));
            oh.setShopAddress("ygn");
            oh.setShopEmail("example_shop@gmail.com");

            entityManager.persist(oh);

            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear(); // free memory
            }
        }

        entityManager.flush();
        entityManager.clear();
    }
}
