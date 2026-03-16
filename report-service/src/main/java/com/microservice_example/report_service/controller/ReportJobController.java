package com.microservice_example.report_service.controller;

import com.microservice_example.report_service.dto.ReportJobResponseDto;
import com.microservice_example.report_service.dto.ReportRequestDto;
import com.microservice_example.report_service.service.ReportJobService;
import com.microservice_example.report_service.util.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/report-jobs")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ReportJobController {

    private final ReportJobService reportJobService;

    @PostMapping
    public ResponseEntity<?> exportDetailReport(@RequestBody ReportRequestDto reportRequestDto) throws IOException {

        try {
            String jobId = reportJobService.createJob();

            reportJobService.processAsyncReportRequest(jobId, reportRequestDto);

            return ResponseEntity.accepted().body(jobId);
        }
        catch (Exception e) {
            log.error("Error occurs : " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ReportJobResponseDto> getReportJob(@PathVariable String jobId) {
        return ResponseEntity.ok().body(reportJobService.getReportJobById(jobId).orElse(null));
    }

    @GetMapping("/{jobId}/status")
    public ResponseEntity<JobStatus> getReportJobStatus(@PathVariable String jobId) {
        return ResponseEntity.ok().body(reportJobService.getReportJobById(jobId)
                .map(ReportJobResponseDto::getStatus).orElse(null));
    }

}
