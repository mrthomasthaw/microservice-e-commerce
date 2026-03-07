package com.microservice_example.report_service.controller;

import com.microservice_example.report_service.dto.ReportRequestDto;
import com.microservice_example.report_service.open_feign_client.CustomerClient;
import com.microservice_example.report_service.open_feign_client.ProductClient;
import com.microservice_example.report_service.open_feign_client.ShopClient;
import com.microservice_example.report_service.service.ReportService;
import com.microservice_example.report_service.service.ReportServiceImpl;
import com.microservice_example.report_service.util.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    private final CustomerClient customerClient;

    private final ProductClient productClient;

    private final ShopClient shopClient;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String reportPage(Model model) {
        model.addAttribute("reportRequestDto", new ReportRequestDto());
        model.addAttribute("reportTypes", ReportType.getAll());
        return "report";
    }

    @GetMapping("/detail-report")
    @ResponseStatus(HttpStatus.OK)
    public String detailReportPage(Model model) {
        var reportRequestDto = new ReportRequestDto();
        reportRequestDto.setReportType(ReportType.DETAIL_REPORT.getCode());
        model.addAttribute("reportRequestDto", reportRequestDto);

        model.addAttribute("customers", customerClient.getAllCustomers());
        model.addAttribute("products", productClient.getAllProducts());
        model.addAttribute("shops", shopClient.getAllShops());
        return "report-detail";
    }

    @PostMapping
    public ResponseEntity<byte[]> exportReport(ReportRequestDto reportRequestDto) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            reportService.exportExcel(reportRequestDto, outputStream);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=report.xlsx")
                    .header("Content-Type", "application/octet-stream")
                    .body(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/detail-report")
    public ResponseEntity<byte[]> exportDetailReport(ReportRequestDto reportRequestDto) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            reportService.exportExcel(reportRequestDto, outputStream);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=report.xlsx")
                    .header("Content-Type", "application/octet-stream")
                    .body(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
