package com.microservice_example.report_service.rabbitmq.consumer;

import com.microservice_example.report_service.dto.EventMessage;
import com.microservice_example.report_service.dto.OrderResponseDto;
import com.microservice_example.report_service.service.ReportService;
import com.microservice_example.report_service.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final ReportService reportService;

    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue("${rabbitmq.queue.name}"), key = "order.created",
                    exchange = @Exchange(value = "${rabbitmq.exchange.name}", type = ExchangeTypes.TOPIC))
    })
    public void consume(EventMessage<Object> eventMessage) {
        log.info("message received : " + eventMessage);

        reportService.createOrderHistory(CommonUtil.convertValue(eventMessage.data(), OrderResponseDto.class));
    }
}
