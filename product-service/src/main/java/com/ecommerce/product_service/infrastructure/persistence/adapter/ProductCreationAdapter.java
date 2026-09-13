package com.ecommerce.product_service.infrastructure.persistence.adapter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.product_service.application.port.ProductCreationPort;
import com.ecommerce.product_service.application.port.ProductEventPublisher;
import com.ecommerce.product_service.application.port.ProductRepository;
import com.ecommerce.product_service.domain.event.ProductCreatedEvent;
import com.ecommerce.product_service.domain.model.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service 
@RequiredArgsConstructor 
public class ProductCreationAdapter implements ProductCreationPort {

    private final ProductRepository repoAdapter;
    private final ProductEventPublisher outboxAdapter;

    @Override
    @Transactional
    public void saveAndPublishEvent(Product product) {
        log.info("%s::saveAndPublishEvent begins.".formatted(getClass().getSimpleName()));

        log.debug("Product is saving. productId: %s".formatted(product.getProductId()));
        repoAdapter.save(product);

        log.debug("ProductCreated event is saving to outbox table. productId: %s".formatted(product.getProductId()));
        outboxAdapter.publish(ProductCreatedEvent.of(product));

        
        log.info("%s::saveAndPublishEvent ends.".formatted(getClass().getSimpleName()));
    }
}
