package com.ecommerce.inventory_service.application.port;

import com.ecommerce.inventory_service.domain.entity.Inventory;

public interface OrderStatusPublisherPort {

    void orderPersistAndCreatedSuccessfuly(Inventory inventory);

    void orderOutOfStock(int orderId);

}
