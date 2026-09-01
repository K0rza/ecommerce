package com.ecommerce.inventory_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import com.ecommerce.inventory_service.application.usecase.OrderCreatedUseCase;
import com.ecommerce.inventory_service.application.usecase.ProductCreateUseCase;
import com.ecommerce.inventory_service.infrastructure.kafka.adapter.KafkaEventPublisherAdapter;
import com.ecommerce.inventory_service.infrastructure.repository.adapter.RepositoryAdapter;

@Controller
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
