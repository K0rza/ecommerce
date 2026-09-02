package com.ecommerce.order_service.application.usecases;

import com.ecommerce.order_service.application.port.PersistServicePort;
import com.ecommerce.order_service.domain.entity.ORDER_STATUS;

public class OrderSuccessfullyCreatedUseCase {

    private final PersistServicePort repositoryPort;

    public OrderSuccessfullyCreatedUseCase(PersistServicePort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public void execute(int orderId) {
        repositoryPort.orderStatus(orderId, ORDER_STATUS.COMPLETED);
    }

}
