package com.ecommerce.inventory_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import com.ecommerce.inventory_service.application.usecase.ProductStock;

@Controller
public class CreateApplicationBean {

    @Bean
    public ProductStock toProductStock() {
        return new ProductStock();
    }

}
