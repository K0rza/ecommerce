package com.ecommerce.order_service.infrastructure.adapters;

import org.springframework.stereotype.Service;

import com.ecommerce.order_service.application.port.OrderCreatedEventPublisher;
import com.ecommerce.order_service.application.port.PersistServicePort;
import com.ecommerce.order_service.domain.entity.ORDER_STATUS;
import com.ecommerce.order_service.domain.entity.Order;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.ecommerce.order_service.infrastructure.persistence.dto.OrderDto;
import com.ecommerce.order_service.infrastructure.persistence.service.DatabaseService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TransactionalPersistServiceAdapter implements PersistServicePort {
     
    private final OrderCreatedEventPublisher publisher;
    private final DatabaseService databaseService;
    
    @Transactional
    @Override
    public void execute(Order order, OrderCreatedEvent event) {
        databaseService.persist(order);
        publisher.publish(event);
    }

    @Override
    public void orderStatus(int orderId, ORDER_STATUS orderStatus) {
        databaseService.findById(orderId).ifPresentOrElse(
            dto -> orderStatusPersist(dto, orderStatus),
            () -> System.err.println("The order not found. orderId: " + orderId)
        );
    }

    private void orderStatusPersist(OrderDto dto, ORDER_STATUS orderStatus) {
        dto.setOrderStatus(orderStatus);

        databaseService.persist(dto);
    }
}
