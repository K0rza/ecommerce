package com.ecommerce.order_service.infrastructure.persistence.dto;

import org.springframework.data.annotation.Id;

import com.ecommerce.order_service.domain.entity.ORDER_STATUS;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name = "OrderOutbox")
@Entity
public class OrderOutboxTable {

    @Id
    private int orderId;
    private int productId;
    private int quantity;
    private ORDER_STATUS status;
    private boolean published;

    public OrderOutboxTable() {}

    private OrderOutboxTable(int orderId, int productId, int quantity, ORDER_STATUS status) {
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

    public static OrderOutboxTable to(OrderCreatedEvent event) {
        return new OrderOutboxTable(event.orderId(), event.productId(), event.quantity(), event.orderStatus());
    }
}
