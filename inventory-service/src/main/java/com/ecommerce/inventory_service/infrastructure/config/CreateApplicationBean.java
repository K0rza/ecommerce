package com.ecommerce.inventory_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.ecommerce.inventory_service.application.usecases.OrderCreatedUseCase;
import com.ecommerce.inventory_service.application.usecases.ProductCreateUseCase;
import com.ecommerce.inventory_service.infrastructure.kafka.adapter.KafkaEventPublisherAdapter;
import com.ecommerce.inventory_service.infrastructure.repository.adapter.OrderRepositoryAdapter;
import com.ecommerce.inventory_service.infrastructure.repository.adapter.ProductRepositoryAdapter;

@Component
public class CreateApplicationBean {

    @Bean
    public ProductCreateUseCase toProductCreateUseCase(ProductRepositoryAdapter repo) {
        return new ProductCreateUseCase(repo);
    }

    @Bean
    public OrderCreatedUseCase toOrderCreatedUseCase(ProductRepositoryAdapter productRepositoryAdapter, OrderRepositoryAdapter orderRepositoryAdapter, KafkaEventPublisherAdapter publisherAdapter) {
        return new OrderCreatedUseCase(productRepositoryAdapter, orderRepositoryAdapter, publisherAdapter);
    } 

}
