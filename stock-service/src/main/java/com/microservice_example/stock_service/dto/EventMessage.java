package com.microservice_example.stock_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

public record EventMessage<T>(String eventName, T data, LocalDateTime eventTime) {

}
