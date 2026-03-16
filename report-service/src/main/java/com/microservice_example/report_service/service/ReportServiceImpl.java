package com.microservice_example.report_service.service;

import com.microservice_example.report_service.dto.*;
import com.microservice_example.report_service.model.OrderHistory;
import com.microservice_example.report_service.open_feign_client.CustomerClient;
import com.microservice_example.report_service.open_feign_client.ProductClient;
import com.microservice_example.report_service.open_feign_client.ShopClient;
import com.microservice_example.report_service.repository.ReportRepository;
import com.microservice_example.report_service.util.CommonExcelUtil;
import com.microservice_example.report_service.util.ReportType;
import io.netty.util.internal.ObjectCleaner;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
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
                        reportRequestDto.getShopName(), reportRequestDto.getFromOrderDate(), reportRequestDto.getToOrderDate()
                        );

                log.info("Fetched row " + list.size());
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

    @Transactional(readOnly = true)
    @Override
    public void exportExcelDetailReport(ReportRequestDto reportRequestDto, OutputStream outputStream) throws IOException {
        Stream<Object[]> reportStream = reportRepository.findOrderHistoryStreamByDetailFilter(
                    reportRequestDto.getCustomerName(), reportRequestDto.getProductCode(),
                    reportRequestDto.getShopName(), reportRequestDto.getFromOrderDate(), reportRequestDto.getToOrderDate()
                    );

        CommonExcelUtil.writeToExcel(List.of(
                "No",
                "Order No",
                "Product Code",
                "Product Name",
                "Qty",
                "Order Date",
                "Shop",
                "Shop Address"
        ), reportStream, outputStream);
    }

//    @Transactional(readOnly = true)
//    @Override
//    public void exportExcelDetailReport(ReportRequestDto reportRequestDto, OutputStream outputStream) throws IOException {
//        AtomicLong count = new AtomicLong(0);
//        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
//
//            log.info("Exporting in batch...");
//            Sheet sheet = workbook.createSheet("Report");
//
//            // Header row
//            Row header = sheet.createRow(0);
//            header.createCell(0).setCellValue("No");
//            header.createCell(1).setCellValue("Order No");
//            header.createCell(2).setCellValue("Product Code");
//            header.createCell(3).setCellValue("Product Name");
//            header.createCell(4).setCellValue("Qty");
//            header.createCell(5).setCellValue("Order Date");
//            header.createCell(6).setCellValue("Shop");
//            header.createCell(7).setCellValue("Shop Address");
//
//            AtomicInteger rowNum = new AtomicInteger(1);
//
//
//            try(Stream<Object[]> rows = reportRepository.findOrderHistoryStreamByDetailFilter(
//                    reportRequestDto.getCustomerName(), reportRequestDto.getProductCode(),
//                    reportRequestDto.getShopName(), reportRequestDto.getFromOrderDate(), reportRequestDto.getToOrderDate()
//                    ))
//            {
//
//                log.info("Fetching in batch...");
//
//                rows.forEach(rowData -> {
//                    log.info("Writing to excel " + count.getAndIncrement());
//
//                    Row row = sheet.createRow(rowNum.get());
//                    row.createCell(0).setCellValue(rowNum.get() + 1);
//                    row.createCell(1).setCellValue(rowData[0].toString());
//                    row.createCell(2).setCellValue(rowData[1].toString());
//                    row.createCell(3).setCellValue(rowData[2].toString());
//                    row.createCell(4).setCellValue(rowData[3].toString());
//                    row.createCell(5).setCellValue(rowData[4].toString());
//                    row.createCell(6).setCellValue(rowData[5].toString());
//                    row.createCell(7).setCellValue(rowData[6].toString());
//
//                    rowNum.getAndIncrement();
//                });
//
//            }
//
//            workbook.write(outputStream);
//            outputStream.flush();
//
//            log.info("Writing in batch...");
//        }
//    }


//    @Override
//    public void exportExcelDetailReport(ReportRequestDto reportRequestDto, OutputStream outputStream) throws IOException {
//        AtomicLong count = new AtomicLong(0);
//        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
//
//            log.info("Exporting in batch...");
//            Sheet sheet = workbook.createSheet("Report");
//
//            // Header row
//            Row header = sheet.createRow(0);
//            header.createCell(0).setCellValue("No");
//            header.createCell(1).setCellValue("Order No");
//            header.createCell(2).setCellValue("Product Code");
//            header.createCell(3).setCellValue("Product Name");
//            header.createCell(4).setCellValue("Qty");
//            header.createCell(5).setCellValue("Order Date");
//            header.createCell(6).setCellValue("Shop");
//            header.createCell(7).setCellValue("Shop Address");
//
//            AtomicInteger rowNum = new AtomicInteger(1);
//
//            int batchSize = 5000;
//            Long lastId = null;
//            while(true)
//            {
//                List<Object[]> rows = reportRepository.findOrderHistoryByDetailFilter(reportRequestDto.getCustomerName(), reportRequestDto.getProductCode(),
//                                        reportRequestDto.getShopName(), reportRequestDto.getFromOrderDate(), reportRequestDto.getToOrderDate(),
//                                        batchSize, lastId);
//
//                log.info("Fetching in batch...");
//
//                if(rows.isEmpty()) break;
//
//                for (Object[] rowData : rows) {
//                    log.info("Writing to excel " + count.getAndIncrement());
//
//                    Row row = sheet.createRow(rowNum.get());
//                    row.createCell(0).setCellValue(rowNum.get() + 1);
//                    row.createCell(1).setCellValue(rowData[1].toString());
//                    row.createCell(2).setCellValue(rowData[2].toString());
//                    row.createCell(3).setCellValue(rowData[3].toString());
//                    row.createCell(4).setCellValue(rowData[4].toString());
//                    row.createCell(5).setCellValue(rowData[5].toString());
//                    row.createCell(6).setCellValue(rowData[6].toString());
//                    row.createCell(7).setCellValue(rowData[7].toString());
//
//                    rowNum.getAndIncrement();
//
//                    lastId = (Long) rowData[0];
//                }
//
//                //lastId = (Long) rows.get(rows.size() - 1)[0];
//                log.info("Last Id : " + lastId);
//            }
//
//            workbook.write(outputStream);
//            outputStream.flush();
//
//            log.info("Writing in batch...");
//        }
//    }



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

    @Override
    public InputStreamResource downloadFile(String jobId) throws IOException {
        Path path = Paths.get("reports/" + jobId + ".xlsx");

        log.info("Path " + path.toString());

        log.info("Abs Path " + path.toAbsolutePath());



        if(! Files.exists(path)) {
            throw new FileNotFoundException(path.toString());
        }

        return new InputStreamResource(Files.newInputStream(path));
    }

//    @Override
//    public void downloadFile(String jobId, OutputStream outputStream) throws IOException {
//        Path path = Paths.get("reports/" + jobId + ".xlsx");
//
//        log.info("Path " + path.toString());
//
//        log.info("Abs Path " + path.toAbsolutePath());
//
//
//        if(Files.exists(path)) {
//
//            try(InputStream inputStream = Files.newInputStream(path)) {
//                outputStream.write(inputStream.readAllBytes());
//            } catch (IOException e) {
//                log.error(e.getMessage());
//                e.printStackTrace();
//            }
//        }
//        else {
//            throw new FileNotFoundException("File not found");
//        }
//
//    }
}
