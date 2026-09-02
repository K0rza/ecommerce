package com.ecommerce.order_service.infrastructure.adapters;

import org.springframework.stereotype.Component;

import com.ecommerce.order_service.application.port.OrderPersistentPort;
import com.ecommerce.order_service.domain.entity.ORDER_STATUS;
import com.ecommerce.order_service.infrastructure.persistence.dto.OrderDto;
import com.ecommerce.order_service.infrastructure.persistence.service.JpaOrderAdapter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JpaOrderPersistentAdapter implements OrderPersistentPort {

    private final JpaOrderAdapter jpaAdapter;

     @Override
    public void orderStatus(int orderId, ORDER_STATUS orderStatus) {
        jpaAdapter.findById(orderId).ifPresentOrElse(
            dto -> orderStatusPersist(dto, orderStatus),
            () -> System.err.println("The order not found. orderId: " + orderId)
        );
    }

    private void orderStatusPersist(OrderDto dto, ORDER_STATUS orderStatus) {
        dto.setOrderStatus(orderStatus);

        jpaAdapter.persist(dto);
    }
}
