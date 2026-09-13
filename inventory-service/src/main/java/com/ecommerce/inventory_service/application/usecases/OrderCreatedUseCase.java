package com.ecommerce.inventory_service.application.usecases;

import com.ecommerce.inventory_service.application.exception.IllegalOrderCreateEventIdempotent;
import com.ecommerce.inventory_service.application.port.in.ProcessOrderUseCase;
import com.ecommerce.inventory_service.application.port.out.OrderRepositoryPort;
import com.ecommerce.inventory_service.application.port.out.OrderStatusPublisherPort;
import com.ecommerce.inventory_service.application.port.out.ProductRepositoryPort;
import com.ecommerce.inventory_service.domain.entity.Inventory;
import com.ecommerce.inventory_service.domain.exception.OutOfStockException;
import com.ecommerce.inventory_service.domain.value.OrderRecord;

public class OrderCreatedUseCase implements ProcessOrderUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final OrderStatusPublisherPort publisher;

    public OrderCreatedUseCase(ProductRepositoryPort repositoryPort, OrderRepositoryPort orderRepositoryPort, OrderStatusPublisherPort publisher) {
        this.productRepositoryPort = repositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.publisher = publisher;
    }

    @Override
    public void process(OrderRecord orderRecord) {
        orderRepositoryPort.ifNewOrderOrElse(
            orderRecord.eventId(),
            () -> processOrderCreateJob(orderRecord), 
            this::rejectOrderCreateJob);
    }

    private void rejectOrderCreateJob(int eventId) {
        throw new IllegalOrderCreateEventIdempotent(eventId);
    }

    private void processOrderCreateJob(OrderRecord orderEvent) {
        try {
            int stock = productRepositoryPort.getStock(orderEvent.productId());

            Inventory inventory = Inventory.of(orderEvent, stock);

            inventory.decreaseStock();

            productRepositoryPort.updateStock(inventory.getProductId(), inventory.getStock());
            
            orderRepositoryPort.eventConsumed(orderEvent.eventId());

            publisher.publisCreatedSuccessfulyEvent(inventory);
             
        } catch (OutOfStockException e) {
            publisher.orderOutOfStock(e.getOrderId());
        }
    }

}
