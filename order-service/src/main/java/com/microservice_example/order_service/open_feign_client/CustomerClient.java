package com.microservice_example.order_service.open_feign_client;

import com.microservice_example.order_service.dto.CustomerResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/customer")
    List<CustomerResponseDto> getAllCustomers();
}
