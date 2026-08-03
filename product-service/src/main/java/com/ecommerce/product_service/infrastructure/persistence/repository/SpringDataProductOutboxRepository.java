package com.ecommerce.product_service.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.product_service.infrastructure.persistence.entity.OutboxEventEntity;

public interface SpringDataProductOutboxRepository extends JpaRepository<OutboxEventEntity, UUID> {

}
