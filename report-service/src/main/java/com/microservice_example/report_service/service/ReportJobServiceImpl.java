package com.microservice_example.report_service.service;

import com.microservice_example.report_service.dto.ReportJobResponseDto;
import com.microservice_example.report_service.dto.ReportRequestDto;
import com.microservice_example.report_service.model.ReportJob;
import com.microservice_example.report_service.repository.ReportJobRepository;
import com.microservice_example.report_service.util.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportJobServiceImpl implements ReportJobService {

    private final ReportService reportService;

    private final ReportJobRepository reportJobRepository;

    @Async("reportExecutor")
    @Override
    public void processAsyncReportRequest(String jobId, ReportRequestDto reportRequestDto) {

        ReportJob reportJob = reportJobRepository.findById(jobId)
                .orElseThrow();

        try {

            Path file = Paths.get("reports/", jobId + ".xlsx");

            Files.createDirectories(file.getParent());

            try(OutputStream outputStream = Files.newOutputStream(file)) {
                reportService.exportExcelDetailReport(reportRequestDto, outputStream);
            }

            reportJob.setStatus(JobStatus.COMPLETED);
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurr : " + e.getMessage());
            reportJob.setStatus(JobStatus.FAILED);
        }
        finally {
            reportJobRepository.save(reportJob);
        }
    }

    @Override
    public String createJob() {
        ReportJob reportJob = ReportJob.builder()
                .id(UUID.randomUUID().toString())
                .status(JobStatus.PENDING)
                .build();

        reportJobRepository.save(reportJob);
        return reportJob.getId();
    }

    @Override
    public Optional<ReportJobResponseDto> getReportJobById(String jobId) {
        return reportJobRepository.findById(jobId)
                .map(this::mapToReportJobResponseDto);
    }

    private ReportJobResponseDto mapToReportJobResponseDto(ReportJob reportJob) {
        return ReportJobResponseDto.builder()
                .jobId(reportJob.getId())
                .status(reportJob.getStatus())
                .createdTime(reportJob.getCreatedTime())
                .filePath(reportJob.getFilePath())
                .build();
    }
}
