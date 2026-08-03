package com.ecommerce.product_service.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Repository;

import com.ecommerce.product_service.domain.event.ProductCreatedEvent;
import com.ecommerce.product_service.domain.model.Product;
import com.ecommerce.product_service.domain.repository.ProductRepository;
import com.ecommerce.product_service.infrastructure.persistence.entity.OutboxEventEntity;
import com.ecommerce.product_service.infrastructure.persistence.entity.ProductJpaEntity;
import com.ecommerce.product_service.infrastructure.persistence.repository.SpringDataProductOutboxRepository;
import com.ecommerce.product_service.infrastructure.persistence.repository.SpringDataProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class JpaProductRepositoryAndConsistencyAdapter implements ProductRepository {

    private final SpringDataProductRepository jpaRepo;
    private final SpringDataProductOutboxRepository outboxRepo;
    private final ObjectMapper mapper;

    @Override
    @Transactional
    public void save(Product product, ProductCreatedEvent event) {
        log.error("Data is saving...");
        save(product);
        log.error("Data is saved.");
        
        log.error("Outbox event is saving...");
        save(event);
        log.error("Outbox event is saved.");
    }

    @Override
    public Optional<Product> findById(int productId) {
        return jpaRepo.findById(productId).map(ProductJpaEntity::toDomain);
    }

    public Stream<OutboxEventEntity> findNotProcessedEvents() {
        return outboxRepo.findAll().stream()
            .filter(OutboxEventEntity::isNotProcessed);
    }

    public void processed(UUID id) {
        outboxRepo.findById(id).ifPresentOrElse(entity -> {
            entity.processed();
            outboxRepo.save(entity);
        }, () -> log.error("Couldnt find the data from the database. id: %s", id));
    }

    private void save(Product product) {
        ProductJpaEntity entity = ProductJpaEntity.fromDomain(product);
        jpaRepo.save(entity);
    }

    private void save(ProductCreatedEvent event) {
        String payload;
        try {
            payload = mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            payload = "CANNOT CONVERT INTO JSON";
        }

        OutboxEventEntity outboxEntity = OutboxEventEntity.of(event.productId(), "PRODUCT-CREATED-EVENTS", payload);
        log.error("outbox entity: "+ outboxEntity);
        outboxRepo.save(outboxEntity);
    }
}
