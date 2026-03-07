package com.microservice_example.report_service.open_feign_client;

import com.microservice_example.report_service.dto.CustomerResponseDto;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/customer")
    List<CustomerResponseDto> getAllCustomers();

    @RequestMapping(method = RequestMethod.GET, value = "/api/customer/{id}")
    Optional<CustomerResponseDto> getCustomerById(@PathVariable("id") Long id);

}
