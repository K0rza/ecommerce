package com.ecommerce.api_gateway.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.ecommerce.api_gateway.application.usecase.AuthenticationUseCase;

@Component
public class BeanFactory {

    @Bean
    public AuthenticationUseCase to() {
        return new AuthenticationUseCase();
    }
}
