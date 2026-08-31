package com.ecommerce.order_service.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.ecommerce.order_service.application.usecases.CreateOrderUseCase;
import com.ecommerce.order_service.infrastructure.adapters.TransactionalPersistServiceAdapter;

@Component
public class BeanFactory {

    @Bean
    public CreateOrderUseCase toUseCase(TransactionalPersistServiceAdapter adapter) {
        return new CreateOrderUseCase(adapter);
    }
}
