package com.ecommerce.inventory_service.application.usecase;

import com.ecommerce.inventory_service.application.exception.OutOfStockException;
import com.ecommerce.inventory_service.application.port.OrderStatusPublisherPort;
import com.ecommerce.inventory_service.domain.entity.Inventory;
import com.ecommerce.inventory_service.domain.port.RepositoryPort;
import com.ecommerce.inventory_service.domain.value.OrderRecord;

public class OrderCreatedUseCase {
    private final RepositoryPort repositoryPort;
    private final OrderStatusPublisherPort publisher;

    public OrderCreatedUseCase(RepositoryPort repositoryPort, OrderStatusPublisherPort publisher) {
        this.repositoryPort = repositoryPort;
        this.publisher = publisher;
    }

    public void process(OrderRecord orderEvent) {
        try {
            int stock = repositoryPort.getStock(orderEvent.productId());

            Inventory inventory = Inventory.of(orderEvent, stock);

            inventory.decreaseStock();

            publisher.orderPersistAndCreatedSuccessfuly(inventory);
        } catch (OutOfStockException e) {
            publisher.orderOutOfStock(e.getOrderId());
        }
    }

}
