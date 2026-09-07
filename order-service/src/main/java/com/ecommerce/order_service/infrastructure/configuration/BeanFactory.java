package com.ecommerce.order_service.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.ecommerce.order_service.application.usecases.CreateOrderUseCase;
import com.ecommerce.order_service.application.usecases.OrderOutOfStockUseCase;
import com.ecommerce.order_service.application.usecases.OrderSuccessfullyCreatedUseCase;
import com.ecommerce.order_service.infrastructure.adapters.ApplicationLogger;
import com.ecommerce.order_service.infrastructure.adapters.OrderCreationAdapter;
import com.ecommerce.order_service.infrastructure.adapters.OrderStatusUpdateAdapter;

@Component
public class BeanFactory {

    @Bean
    public CreateOrderUseCase toUseCase(OrderCreationAdapter adapter, ApplicationLogger logger) {
        return new CreateOrderUseCase(adapter, logger);
    }

    @Bean
    public OrderOutOfStockUseCase toOrderOutOfStockUseCase(OrderStatusUpdateAdapter adapter, ApplicationLogger logger) {
        return new OrderOutOfStockUseCase(adapter, logger);
    }

    @Bean
    public OrderSuccessfullyCreatedUseCase toOrderSuccessfullyCreatedUseCase(OrderStatusUpdateAdapter adapter, ApplicationLogger logger) {
        return new OrderSuccessfullyCreatedUseCase(adapter, logger);
    }
}
