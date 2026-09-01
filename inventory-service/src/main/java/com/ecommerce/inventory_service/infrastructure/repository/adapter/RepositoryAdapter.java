package com.ecommerce.inventory_service.infrastructure.repository.adapter;

import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import com.ecommerce.inventory_service.domain.entity.Product;
import com.ecommerce.inventory_service.domain.port.RepositoryPort;
import com.ecommerce.inventory_service.infrastructure.repository.JpaProductInterface;
import com.ecommerce.inventory_service.infrastructure.repository.dto.ProductDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class RepositoryAdapter implements RepositoryPort {

    private final JpaProductInterface productRepo; 

    @Override
    public void ifNewProductOrElse(Product product, Consumer<Product> createNewProductJob, Consumer<Integer> rejectProductJob) {
        log.error("RepositoryAdapter::ifNewProductOrElse begins. eventId: " + product.getEventId());

        productRepo.findByEventId(product.getEventId())
            .ifPresentOrElse(
                p -> rejectProductJob.accept(p.getEventId()), 
                () -> createNewProductJob.accept(product));

        log.error("RepositoryAdapter::ifNewProductOrElse ends.");
    }

    @Transactional
    @Override
    public void create(Product product) {
        log.error("RepositoryAdapter::create begins. productId: " + product.getProductId());

        productRepo.save(ProductDto.from(product));

        log.error("RepositoryAdapter::create ends.");
    }

    @Override
    public int getStock(int productId) {
        return productRepo
            .findById(productId)
            .map(ProductDto::getStock)
            .orElse(0);
    }
}
