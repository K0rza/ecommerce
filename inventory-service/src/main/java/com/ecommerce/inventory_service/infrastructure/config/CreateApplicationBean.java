package com.ecommerce.inventory_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.ecommerce.inventory_service.application.usecases.OrderCreatedUseCase;
import com.ecommerce.inventory_service.application.usecases.ProductCreateUseCase;
import com.ecommerce.inventory_service.infrastructure.kafka.adapter.KafkaEventPublisherAdapter;
import com.ecommerce.inventory_service.infrastructure.repository.adapter.RepositoryAdapter;

@Component
public class CreateApplicationBean {

    @Bean
    public ProductCreateUseCase toProductCreateUseCase(RepositoryAdapter repo) {
        return new ProductCreateUseCase(repo);
    }

    @Bean
    public OrderCreatedUseCase toOrderCreatedUseCase(RepositoryAdapter repositoryAdapter, KafkaEventPublisherAdapter publisherAdapter) {
        return new OrderCreatedUseCase(repositoryAdapter, publisherAdapter);
    } 

}
