package com.fitness.activityservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

/**
 * Configuration class for RabbitMQ integration in the Activity Service.
 * Defines beans for queue, exchange, binding, and message converter.
 */
@Configuration
public class RabbitMqConfig {
    /**
     * Name of the RabbitMQ exchange, injected from application properties.
     */
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    /**
     * Name of the RabbitMQ queue, injected from application properties.
     */
    @Value("${rabbitmq.queue.name}")
    private String queueName;
    /**
     * Routing key for RabbitMQ, injected from application properties.
     */
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    /**
     * Declares the activity queue.
     * @return a durable Queue instance
     */
    @Bean
    public Queue activityQueue(){
        return  new Queue(queueName, true);
    }

    /**
     * Configures the message converter to use Jackson for JSON serialization.
     * @return a JacksonJsonMessageConverter instance
     */
    @Bean
    public MessageConverter messageConverter(){
        return new JacksonJsonMessageConverter();
    }

    /**
     * Declares the direct exchange for activities.
     * @return a DirectExchange instance
     */
    @Bean
    public DirectExchange activityExchange(){
        return  new DirectExchange(exchangeName);
    }

    /**
     * Binds the activity queue to the exchange with the routing key.
     * @param activityQueue the queue bean
     * @param activityExchange the exchange bean
     * @return a Binding instance
     */
    @Bean
    public Binding activityBinding(Queue activityQueue, DirectExchange activityExchange){
        return BindingBuilder.bind(activityQueue).to(activityExchange).with(routingKey);
    }
}
