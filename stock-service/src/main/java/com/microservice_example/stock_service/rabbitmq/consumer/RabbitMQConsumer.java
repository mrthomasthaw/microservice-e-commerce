package com.microservice_example.stock_service.rabbitmq.consumer;

import com.microservice_example.stock_service.dto.EventMessage;
import com.microservice_example.stock_service.dto.OrderResponseDto;
import com.microservice_example.stock_service.rabbitmq.producer.RabbitMQProducer;
import com.microservice_example.stock_service.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    private final StockService stockService;
    private final RabbitMQProducer rabbitMQProducer;

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(EventMessage<?> message) {
        log.info("message received: {}", message);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            OrderResponseDto orderResponseDto = objectMapper.convertValue(message.data(), OrderResponseDto.class);
            if(message.eventName().equals("order.created")) {
                stockService.updateStockQty(orderResponseDto);
                rabbitMQProducer.sendMessage(new EventMessage<OrderResponseDto>("stock.updated", null, LocalDateTime.now()),
                        "stock.updated");
            }
        }
        catch (Exception e) {
            rabbitMQProducer.sendMessage(new EventMessage<OrderResponseDto>("stock.update-failed", null, LocalDateTime.now()),
                    "stock.update-failed");

            e.printStackTrace();
            log.error("error occurs when updating stock : " + e.getMessage());
        }
    }

}
