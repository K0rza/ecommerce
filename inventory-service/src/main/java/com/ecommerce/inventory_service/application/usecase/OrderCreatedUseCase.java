package com.ecommerce.inventory_service.application.usecase;

import com.ecommerce.inventory_service.application.exception.OutOfStockException;
import com.ecommerce.inventory_service.application.port.DatabaseServicePort;
import com.ecommerce.inventory_service.application.port.OrderStatusPublisherPort;
import com.ecommerce.inventory_service.domain.entity.Inventory;
import com.ecommerce.inventory_service.domain.value.OrderRecord;

public class OrderCreatedUseCase {
    private final DatabaseServicePort dbService;
    private final OrderStatusPublisherPort publisher;

    public OrderCreatedUseCase(DatabaseServicePort dbService, OrderStatusPublisherPort publisher) {
        this.dbService = dbService;
        this.publisher = publisher;
    }

    public void process(OrderRecord orderEvent) {
        try {
            int stock = dbService.getStock(orderEvent.productId());

            Inventory inventory = Inventory.of(orderEvent, stock);

            inventory.decreaseStock();

            publisher.orderPersistAndCreatedSuccessfuly(inventory);
        } catch (OutOfStockException e) {
            publisher.orderOutOfStock(e.getOrderId());
        }
    }

}
