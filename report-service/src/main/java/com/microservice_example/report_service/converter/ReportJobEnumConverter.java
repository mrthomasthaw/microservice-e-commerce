package com.microservice_example.report_service.converter;

import com.microservice_example.report_service.model.ReportJob;
import com.microservice_example.report_service.util.JobStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Optional;

@Converter(autoApply = true)
public class ReportJobEnumConverter implements AttributeConverter<JobStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(JobStatus status) {
        if (status == null) return null;
        return status.getCode();
    }

    @Override
    public JobStatus convertToEntityAttribute(Integer code) {
        return JobStatus.getByCode(code).orElse(null);
    }
}
