package com.ecommerce.order_service.infrastructure.persistence.dto;

import com.ecommerce.order_service.domain.entity.ORDER_STATUS;
import com.ecommerce.order_service.domain.entity.Order;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderTable {

    @Id
    private int orderId;
    private String customerId;
    private int productId;
    private int quantity;
    private ORDER_STATUS status;

    private OrderTable(int orderId, String customerId, int productId, int quantity, ORDER_STATUS status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
    }

    public static OrderTable fromDomain(Order order) {
        return new OrderTable(order.getOrderId(), order.getCustomerId(), order.getProductId(), order.getQuantity(), order.getStatus());
    }

}
