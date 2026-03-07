package com.microservice_example.report_service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportRequestDto {

    private Integer reportType;

    private String customerName;

    private String productCode;

    private String shopName;

    private LocalDate fromOrderDate;

    private LocalDate toOrderDate;
}
