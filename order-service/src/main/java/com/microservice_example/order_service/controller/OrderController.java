package com.microservice_example.order_service.controller;

import com.microservice_example.order_service.dto.OrderRequestDto;
import com.microservice_example.order_service.dto.OrderResponseDto;
import com.microservice_example.order_service.rabbitmq.consumer.RabbitMQConsumer;
import com.microservice_example.order_service.rabbitmq.producer.RabbitMQProducer;
import com.microservice_example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    private final RabbitMQProducer rabbitMQProducer;

    private final RabbitMQConsumer rabbitMQConsumer;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponseDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        orderService.createOrder(orderRequestDto);
    }


    @GetMapping("/publish-json")
    public String sendJsonMessage(@RequestBody OrderRequestDto orderRequestDto) {
        rabbitMQProducer.sendMessage(orderRequestDto, "order.created");
        return "json message send to rabbitmq...";
    }

    @GetMapping("/get-message")
    @ResponseStatus(HttpStatus.OK)
    public void getMessage() {
        rabbitMQConsumer.receivedMessage();
    }

    @GetMapping("/get-json-message")
    @ResponseStatus(HttpStatus.OK)
    public void getJsonMessage() {
        rabbitMQConsumer.receivedJsonMessage();
    }
}
