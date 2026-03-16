package com.microservice_example.report_service.controller;

import com.microservice_example.report_service.dto.ReportJobResponseDto;
import com.microservice_example.report_service.dto.ReportRequestDto;
import com.microservice_example.report_service.open_feign_client.CustomerClient;
import com.microservice_example.report_service.open_feign_client.ProductClient;
import com.microservice_example.report_service.open_feign_client.ShopClient;
import com.microservice_example.report_service.service.ReportJobService;
import com.microservice_example.report_service.service.ReportService;
import com.microservice_example.report_service.util.JobStatus;
import com.microservice_example.report_service.util.ReportType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.UUID;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    private final CustomerClient customerClient;

    private final ProductClient productClient;

    private final ShopClient shopClient;

    private final ReportJobService reportJobService;



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

        //model.addAttribute("customers", customerClient.getAllCustomers());
        //model.addAttribute("products", productClient.getAllProducts());
        //model.addAttribute("shops", shopClient.getAllShops());
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

        } catch (Exception e) {
            log.error("Error occurs : " + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/{jobId}/download")
    public ResponseEntity<InputStreamResource> downloadReport(@PathVariable String jobId) throws FileNotFoundException {

        try{
            InputStreamResource inputStreamResource = reportService.downloadFile(jobId);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/octet-stream")
                    .header("Content-Disposition", "attachment; filename=report.xlsx")
                    .body(inputStreamResource);
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurs : " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

//    @PostMapping("/detail-report")
//    public ResponseEntity<byte[]> exportReportDetail(ReportRequestDto reportRequestDto) {
//        try {
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            reportService.exportExcel(reportRequestDto, outputStream);
//            return ResponseEntity.ok()
//                    .header("Content-Disposition", "attachment; filename=report.xlsx")
//                    .header("Content-Type", "application/octet-stream")
//                    .body(outputStream.toByteArray());
//
//        } catch (Exception e) {
//            log.error("Error occurs : " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//    }

//    @PostMapping("/detail-report")
//    public ResponseEntity<InputStreamResource> exportDetailReport(
//            ReportRequestDto request) throws IOException {
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        // generate excel
//        reportService.exportExcel(request, outputStream);
//
//        ByteArrayInputStream inputStream =
//                new ByteArrayInputStream(outputStream.toByteArray());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType(
//                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
//        headers.setContentDisposition(
//                ContentDisposition.builder("attachment")
//                        .filename("report.xlsx")
//                        .build());
//
//        InputStreamResource resource = new InputStreamResource(inputStream);
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(resource);
//    }

//    @PostMapping("/detail-report")
//    public ResponseEntity<InputStreamResource> exportDetailReport(
//            ReportRequestDto request) throws IOException {
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        // generate excel
//        reportService.exportExcelDetailReport(request, outputStream);
//
//        ByteArrayInputStream inputStream =
//                new ByteArrayInputStream(outputStream.toByteArray());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType(
//                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
//        headers.setContentDisposition(
//                ContentDisposition.builder("attachment")
//                        .filename("report.xlsx")
//                        .build());
//
//        InputStreamResource resource = new InputStreamResource(inputStream);
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(resource);
//    }





//    @PostMapping("/detail-report")
//    public ResponseEntity<StreamingResponseBody> exportDetailReport(
//            ReportRequestDto request) {
//
//        StreamingResponseBody stream = outputStream -> {
//            reportService.exportExcelDetailReport(request, outputStream);
//        };
//
//        return ResponseEntity.ok()
//                .header("Content-Disposition", "attachment; filename=report.xlsx")
//                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
//                .body(stream);
//    }


//    @GetMapping("/generate-data")
//    @ResponseBody
//    public void generateData() {
//        reportService.generateDataset(1000000);
//    }
}
