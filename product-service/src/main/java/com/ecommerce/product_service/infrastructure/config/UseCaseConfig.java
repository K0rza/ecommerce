package com.ecommerce.product_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecommerce.product_service.application.usecase.CreateProductUseCase;
import com.ecommerce.product_service.domain.repository.ProductRepository;
import com.ecommerce.product_service.infrastructure.kafka.adapter.KafkaProductCreatedEventPublisherAdapter;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateProductUseCase CreateProductUseCase(ProductRepository repo, KafkaProductCreatedEventPublisherAdapter eventPublisher) {
        return new CreateProductUseCase(repo, eventPublisher);
    }

}
