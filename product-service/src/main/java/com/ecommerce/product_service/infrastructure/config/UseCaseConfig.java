package com.ecommerce.product_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecommerce.product_service.application.usecase.CreateProductUseCase;
import com.ecommerce.product_service.infrastructure.persistence.adapter.JpaOutboxRepositoryAdapter;
import com.ecommerce.product_service.infrastructure.persistence.adapter.JpaProductRepositoryAdapter;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateProductUseCase CreateProductUseCase(JpaProductRepositoryAdapter repo, JpaOutboxRepositoryAdapter publisher) {
        return new CreateProductUseCase(repo, publisher);
    }

}
