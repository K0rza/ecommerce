package com.ecommerce.inventory_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import com.ecommerce.inventory_service.application.usecase.ProductCreateUseCase;
import com.ecommerce.inventory_service.application.usecase.ProductStock;
import com.ecommerce.inventory_service.infrastructure.adapter.RepositoryAdapter;

@Controller
public class CreateApplicationBean {

    @Bean
    public ProductStock toProductStock() {
        return new ProductStock();
    }

    @Bean
    public ProductCreateUseCase toProductCreateUseCase(RepositoryAdapter repo) {
        return new ProductCreateUseCase(repo);
    }

}
