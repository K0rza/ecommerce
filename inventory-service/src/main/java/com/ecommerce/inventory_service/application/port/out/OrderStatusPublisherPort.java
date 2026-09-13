package com.ecommerce.inventory_service.application.port.out;

import com.ecommerce.inventory_service.domain.entity.Inventory;

public interface OrderStatusPublisherPort {

    void orderOutOfStock(int orderId);

    void publisCreatedSuccessfulyEvent(Inventory inventory);

}
