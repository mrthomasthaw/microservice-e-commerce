package com.microservice_example.order_service.rabbitmq.consumer;

import com.microservice_example.order_service.dto.EventMessage;
import com.microservice_example.order_service.dto.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rabbitmq.queue.name}", durable = "true"),
            exchange = @Exchange(value = "${rabbitmq.exchange.name}", type = ExchangeTypes.TOPIC),
            key = {"stock.updated", "stock.update-failed"}
    ))
    public void consume(EventMessage<?> message) {
        if(message.eventName().equals("stock.updated")) {
            log.info("Stock updated event from stock service");
        }
        else if(message.eventName().equals("stock.update-failed")) {
            log.info("Stock update failed event from stock service");
        }


        log.info(String.format("Received Message: %s", message));
    }

    public void receivedMessage() {
        Object message = rabbitTemplate.receive(queueName);
        log.info("Received message: " + message);
    }

    public void receivedJsonMessage() {
        OrderRequestDto message = (OrderRequestDto) rabbitTemplate.receiveAndConvert("javaguides_json");
        log.info("Received message: " + message.toString());
    }
}
