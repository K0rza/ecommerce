package com.ecommerce.order_service.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.ecommerce.order_service.application.port.OrderCreatedEventPublisher;
import com.ecommerce.order_service.application.usecases.CreateOrderUseCase;

@Component
public class BeanFactory {

    @Bean
    public CreateOrderUseCase toUseCase(OrderCreatedEventPublisher publisher) {
        return new CreateOrderUseCase(publisher);
    }

    /*
        @Bean
        public OrderCreatedEventPublisher toPublisher(OrderCreatedEventPublisherAdapter adapter) {
            return adapter;
        }
    */
}
