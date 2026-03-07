package com.microservice_example.report_service.dto;

import java.time.LocalDateTime;


public record EventMessage<T>(String eventName, T data, LocalDateTime eventTime) {

}
