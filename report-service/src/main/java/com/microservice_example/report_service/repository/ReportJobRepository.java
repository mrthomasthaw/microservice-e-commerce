package com.microservice_example.report_service.repository;

import com.microservice_example.report_service.model.ReportJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportJobRepository extends JpaRepository<ReportJob, String> {
}
