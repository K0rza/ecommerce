package com.ecommerce.product_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecommerce.product_service.application.usecases.CreateProductUseCase;
import com.ecommerce.product_service.infrastructure.persistence.adapter.ProductCreationAdapter;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateProductUseCase CreateProductUseCase(ProductCreationAdapter adapter) {
        return new CreateProductUseCase(adapter);
    }

}
