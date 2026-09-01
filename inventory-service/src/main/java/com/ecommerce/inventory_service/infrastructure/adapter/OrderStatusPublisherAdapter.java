package com.ecommerce.inventory_service.infrastructure.adapter;

import org.springframework.stereotype.Service;

import com.ecommerce.inventory_service.application.port.OrderStatusPublisherPort;
import com.ecommerce.inventory_service.domain.entity.Inventory;
import com.ecommerce.inventory_service.domain.port.RepositoryPort;
import com.ecommerce.inventory_service.infrastructure.kafka.event.writer.OrderEventPublisher;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderStatusPublisherAdapter implements OrderStatusPublisherPort {

    private final RepositoryPort repositoryPort; 
    private final OrderEventPublisher publisher;

    @Transactional
    @Override
    public void orderPersistAndCreatedSuccessfuly(Inventory inventory) {
       repositoryPort.updateStock(inventory.getProductId(), inventory.getStock());
       publisher.publishOrderCreated(inventory.getOrderId());
    }

    @Override
    public void orderOutOfStock(int orderId) {
        publisher.publishOutOfStock(orderId);
    }

}
