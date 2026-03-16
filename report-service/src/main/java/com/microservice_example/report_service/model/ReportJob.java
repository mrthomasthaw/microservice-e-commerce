package com.microservice_example.report_service.model;

import com.microservice_example.report_service.converter.ReportJobEnumConverter;
import com.microservice_example.report_service.util.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_report_job")
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportJob {

    @Id
    private String id;
    private String filePath;

    @Convert(converter = ReportJobEnumConverter.class)
    private JobStatus status;

    @CreatedDate
    private LocalDateTime createdTime;
}
