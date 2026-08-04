package com.ecommerce.product_service.infrastructure.persistence.adapter;

import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.ecommerce.product_service.application.port.ProductEventPublisher;
import com.ecommerce.product_service.domain.event.ProductCreatedEvent;
import com.ecommerce.product_service.infrastructure.persistence.entity.OutboxEventEntity;
import com.ecommerce.product_service.infrastructure.persistence.repository.SpringDataProductOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JpaOutboxRepositoryAdapter implements ProductEventPublisher {

    private final SpringDataProductOutboxRepository outboxRepo;
    private final ObjectMapper mapper;

    @Override
    public void publish(ProductCreatedEvent event) {
        log.error("Outbox event is saving...");
        save(event);
        log.error("Outbox event is saved.");
    }
    
    @Override
    public Stream<OutboxEventEntity> findNotProcessed() {
        return outboxRepo.findAll().stream()
            .filter(OutboxEventEntity::isNotProcessed);
    }

    @Override
    public void processed(UUID id) {
        outboxRepo.findById(id).ifPresentOrElse(entity -> {
            entity.processed();
            outboxRepo.save(entity);
        }, () -> log.error("Couldnt find the data from the database. id: %s", id));
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
