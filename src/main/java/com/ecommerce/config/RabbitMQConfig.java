
package com.ecommerce.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String ORDER_CREATED_QUEUE =
            "order.created.queue";

    public static final String ORDER_HISTORY_QUEUE =
            "order.events.history.queue";

    public static final String STATISTICS_QUEUE =
            "statistics.queue";

    public static final String ORDER_REQUEST_QUEUE =
            "order.request.queue";

    public static final String ORDER_EXCHANGE =
            "order.events.exchange";

    public static final String ORDER_CREATED_ROUTING_KEY =
            "order.created";

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(ORDER_CREATED_QUEUE, true);
    }

    @Bean
    public Queue orderHistoryQueue() {
        return new Queue(ORDER_HISTORY_QUEUE, true);
    }

    @Bean
    public Queue statisticsQueue() {
        return new Queue(STATISTICS_QUEUE, true);
    }

    @Bean
    public Queue orderRequestQueue() {
        return new Queue(ORDER_REQUEST_QUEUE, true);
    }

    @Bean
    public DirectExchange orderEventsExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder
                .bind(orderCreatedQueue())
                .to(orderEventsExchange())
                .with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding orderHistoryBinding() {
        return BindingBuilder
                .bind(orderHistoryQueue())
                .to(orderEventsExchange())
                .with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding statisticsBinding() {
        return BindingBuilder
                .bind(statisticsQueue())
                .to(orderEventsExchange())
                .with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        RabbitTemplate template =
                new RabbitTemplate(connectionFactory);

        template.setMessageConverter(messageConverter);

        return template;
    }
}
