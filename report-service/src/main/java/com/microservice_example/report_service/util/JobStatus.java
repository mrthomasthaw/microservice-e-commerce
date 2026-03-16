package com.microservice_example.report_service.util;

import lombok.Getter;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Optional;

@Getter
public enum JobStatus {

    PENDING(1, "Pending"), PROCESSING(2, "Processing"),
    COMPLETED(3, "Completed"), FAILED(4, "Failed");

    int code;
    String name;

    JobStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Optional<JobStatus> getByCode(Integer code) {
        return Arrays.stream(JobStatus.values()).filter(j -> j.getCode() == code).findFirst();
    }
}
