package com.ecommerce.order_service.application.usecases;

import com.ecommerce.order_service.application.port.OrderPersistentPort;
import com.ecommerce.order_service.domain.entity.ORDER_STATUS;

public class OrderSuccessfullyCreatedUseCase {

    private final OrderPersistentPort repositoryPort;

    public OrderSuccessfullyCreatedUseCase(OrderPersistentPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public void execute(int orderId) {
        repositoryPort.orderStatus(orderId, ORDER_STATUS.COMPLETED);
    }

}
