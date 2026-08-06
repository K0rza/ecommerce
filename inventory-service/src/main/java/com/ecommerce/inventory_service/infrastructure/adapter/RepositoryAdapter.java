package com.ecommerce.inventory_service.infrastructure.adapter;

import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import com.ecommerce.inventory_service.domain.entity.Product;
import com.ecommerce.inventory_service.domain.port.RepositoryPort;
import com.ecommerce.inventory_service.infrastructure.repository.JpaProductInterface;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RepositoryAdapter implements RepositoryPort {

    private JpaProductInterface productRepo; 

    @Override
    public void ifNewProductOrElse(Product product, Consumer<Product> createNewProductJob, Runnable rejectProductJob) {
        productRepo.findAll().stream()
            .filter(item -> item.getEventId() == product.getEventId())
            .findFirst()
            .ifPresentOrElse(
                ignore -> rejectProductJob.run(), 
                () -> createNewProductJob.accept(product));
    }
}
