package com.microservice_example.stock_service.util;

import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class CommonUtil {

    private static ObjectMapper objectMapper;


    public static ObjectMapper getObjectMapper() {
        if(objectMapper == null) {
            objectMapper = new ObjectMapper();
        }

        return objectMapper;
    }

    public static <T> T convertValue(Object from, Class<T> toValue) {
        return getObjectMapper().convertValue(from, toValue);
    }
}
