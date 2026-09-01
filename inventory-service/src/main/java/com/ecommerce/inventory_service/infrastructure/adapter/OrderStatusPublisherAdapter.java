package com.ecommerce.inventory_service.infrastructure.adapter;

import org.springframework.stereotype.Service;

import com.ecommerce.inventory_service.application.port.OrderStatusPublisherPort;
import com.ecommerce.inventory_service.domain.entity.Inventory;
import com.ecommerce.inventory_service.infrastructure.kafka.event.writer.OrderEventPublisher;
import com.ecommerce.inventory_service.infrastructure.repository.JpaProductInterface;
import com.ecommerce.inventory_service.infrastructure.repository.dto.ProductDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderStatusPublisherAdapter implements OrderStatusPublisherPort {

    private final JpaProductInterface jpaAdapter; 
    private final OrderEventPublisher publisher;

    @Transactional
    @Override
    public void orderPersistAndCreatedSuccessfuly(Inventory inventory) {
       jpaAdapter.save(ProductDto.from(inventory));
       publisher.publishOrderCreated(inventory.getOrderId());
    }

    @Override
    public void orderOutOfStock(int orderId) {
        publisher.publishOutOfStock(orderId);
    }

}
