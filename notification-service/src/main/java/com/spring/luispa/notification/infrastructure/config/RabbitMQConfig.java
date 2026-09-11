package com.spring.luispa.notification.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.queue.name}")
    private String queueName;

    @Value("${app.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${app.rabbitmq.routing-key.name}")
    private String routingKey;

    @Bean
    public Queue notificationQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public DirectExchange flashSaleExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange flashSaleExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(flashSaleExchange)
                .with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
