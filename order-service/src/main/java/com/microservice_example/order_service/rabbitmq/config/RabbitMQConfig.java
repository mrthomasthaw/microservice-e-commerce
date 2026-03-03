package com.microservice_example.order_service.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    private String queue;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;


    @Bean
    public Queue queue() {
        return new Queue(queue);
    }


    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    //bind queue(message storage) and exchange(queue router) with routing key i.e this routing key will go
    //to this queue
    @Bean
    public Binding stockUpdateBinding() {
        return BindingBuilder.bind(queue())
                .to(exchange())
                .with("stock.updated");
    }

    @Bean
    public Binding stockUpdateFailedBinding() {
        return BindingBuilder.bind(queue())
                .to(exchange())
                .with("stock.update-failed");
    }



    //Spring boot auto configuration makes all of these beans without having to create by yourself
    //connection factory
    //rabbit template (default auto configuration doesn't support json message format)
    //rabbit admin

    @Bean
    public MessageConverter converter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
