package com.microservice_example.report_service.service;

import com.microservice_example.report_service.dto.ReportJobResponseDto;
import com.microservice_example.report_service.dto.ReportRequestDto;

import java.util.Optional;

public interface ReportJobService {
    void processAsyncReportRequest(String jobId, ReportRequestDto reportRequestDto);

    String createJob();

    Optional<ReportJobResponseDto> getReportJobById(String jobId);
}
