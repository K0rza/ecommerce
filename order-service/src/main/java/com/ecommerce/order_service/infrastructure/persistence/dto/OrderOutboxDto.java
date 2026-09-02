package com.ecommerce.order_service.infrastructure.persistence.dto;

import com.ecommerce.order_service.domain.entity.ORDER_STATUS;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.ecommerce.order_service.infrastructure.kafka.contract.OrderEvent;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "OrderOutbox")
@Entity
public class OrderOutboxDto {

    @Id
    private int orderId;
    private int productId;
    private int quantity;
    private ORDER_STATUS status;
    private boolean published;

    public OrderOutboxDto() {}

    private OrderOutboxDto(int orderId, int productId, int quantity, ORDER_STATUS status) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.published = false;
    }

    public void published() { this.published = true; }

    public int getOrderId() {  return orderId;  }

    public int getProductId() {  return productId;  }

    public int getQuantity() {  return quantity;  }

    public ORDER_STATUS getStatus() {  return status;  }

    public boolean isNotPublished() { return !published;  }

    public static OrderOutboxDto to(OrderCreatedEvent event) {
        return new OrderOutboxDto(event.orderId(), event.productId(), event.quantity(), event.orderStatus());
    }

    public OrderEvent toKafkaEvent() {
        return new OrderEvent(orderId, productId, quantity);
    }
}
