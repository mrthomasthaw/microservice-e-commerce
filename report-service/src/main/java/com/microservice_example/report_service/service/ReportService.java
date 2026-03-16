package com.microservice_example.report_service.service;

import com.microservice_example.report_service.dto.OrderResponseDto;
import com.microservice_example.report_service.dto.ReportRequestDto;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public interface ReportService {

    void createOrderHistory(OrderResponseDto orderResponseDto);

    void exportExcel(ReportRequestDto reportRequestDto, OutputStream outputStream) throws IOException;

    void exportExcelDetailReport(ReportRequestDto reportRequestDto, OutputStream outputStream) throws IOException;


    void generateDataset(int totalRows);

    //void downloadFile(String jobId, OutputStream outputStream) throws IOException;

    InputStreamResource downloadFile(String jobId) throws IOException;
}
