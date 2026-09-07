package com.ecommerce.order_service.infrastructure.adapters;

import org.springframework.stereotype.Component;

import com.ecommerce.order_service.application.port.OrderStatusUpdatePort;
import com.ecommerce.order_service.domain.entity.ORDER_STATUS;
import com.ecommerce.order_service.infrastructure.persistence.dto.OrderDto;
import com.ecommerce.order_service.infrastructure.persistence.service.OrderPersistenceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class OrderStatusUpdateAdapter implements OrderStatusUpdatePort {

    private final OrderPersistenceService orderPersistenceService;

    @Override
    public void updateStatus(int orderId, ORDER_STATUS status) {
        orderPersistenceService.findById(orderId).ifPresentOrElse(
            dto -> applyStatus(dto, status),
            () -> log.error("%s::updateStatus The order not found. orderId: %s".formatted(this.getClass().getSimpleName(), orderId))
        );
    }

    private void applyStatus(OrderDto dto, ORDER_STATUS status) {
        log.info("%s::applyStatus begins.".formatted(this.getClass().getSimpleName()));

        dto.setOrderStatus(status);

        orderPersistenceService.persist(dto);
        log.info("%s::applyStatus order response persisted to db. %s".formatted(this.getClass().getSimpleName(), dto));

        log.info("%s::applyStatus ends.".formatted(this.getClass().getSimpleName()));
    }
}
