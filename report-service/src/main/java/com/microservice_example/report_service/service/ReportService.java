package com.microservice_example.report_service.service;

import com.microservice_example.report_service.dto.OrderResponseDto;
import com.microservice_example.report_service.dto.ReportRequestDto;

import java.io.IOException;
import java.io.OutputStream;

public interface ReportService {

    void createOrderHistory(OrderResponseDto orderResponseDto);

    void exportExcel(ReportRequestDto reportRequestDto, OutputStream outputStream) throws IOException;

    void generateDataset(int totalRows);
}
