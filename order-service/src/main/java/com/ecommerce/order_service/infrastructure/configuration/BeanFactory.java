package com.ecommerce.order_service.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.ecommerce.order_service.application.usecases.CreateOrderUseCase;
import com.ecommerce.order_service.application.usecases.OrderOutOfStockUseCase;
import com.ecommerce.order_service.application.usecases.OrderSuccessfullyCreatedUseCase;
import com.ecommerce.order_service.infrastructure.adapters.JpaOrderPersistentAdapter;
import com.ecommerce.order_service.infrastructure.adapters.OutboxEventPublisherAdapter;

@Component
public class BeanFactory {

    @Bean
    public CreateOrderUseCase toUseCase(OutboxEventPublisherAdapter adapter) {
        return new CreateOrderUseCase(adapter);
    }

    @Bean
    public OrderOutOfStockUseCase toOrderOutOfStockUseCase(JpaOrderPersistentAdapter adapter) {
        return new OrderOutOfStockUseCase(adapter);
    }
    
    @Bean
    public OrderSuccessfullyCreatedUseCase toOrderSuccessfullyCreatedUseCase(JpaOrderPersistentAdapter adapter) {
        return new OrderSuccessfullyCreatedUseCase(adapter);
    }
}
