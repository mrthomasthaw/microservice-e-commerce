package com.microservice_example.report_service.dto;

import com.microservice_example.report_service.util.JobStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportJobResponseDto {

    private String jobId;
    private String filePath;
    private JobStatus status;
    private LocalDateTime createdTime;
}
