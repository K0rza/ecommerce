package com.ecommerce.order_service.infrastructure.adapters;

import org.springframework.stereotype.Component;

import com.ecommerce.order_service.application.port.OrderStatusUpdatePort;
import com.ecommerce.order_service.domain.entity.ORDER_STATUS;
import com.ecommerce.order_service.infrastructure.persistence.dto.OrderDto;
import com.ecommerce.order_service.infrastructure.persistence.service.OrderPersistenceService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderStatusUpdateAdapter implements OrderStatusUpdatePort {

    private final OrderPersistenceService orderPersistenceService;

    @Override
    public void updateStatus(int orderId, ORDER_STATUS status) {
        orderPersistenceService.findById(orderId).ifPresentOrElse(
            dto -> applyStatus(dto, status),
            () -> System.err.println("The order not found. orderId: " + orderId)
        );
    }

    private void applyStatus(OrderDto dto, ORDER_STATUS status) {
        dto.setOrderStatus(status);

        orderPersistenceService.persist(dto);
    }
}
