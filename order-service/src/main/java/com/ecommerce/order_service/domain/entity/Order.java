package com.ecommerce.order_service.domain.entity;

import com.ecommerce.order_service.domain.exceptions.IllegalCustomerIdException;
import com.ecommerce.order_service.domain.exceptions.IllegalOrderIdException;
import com.ecommerce.order_service.domain.exceptions.IllegalProductIdException;
import com.ecommerce.order_service.domain.exceptions.IllegalQuantityException;

public class Order {

    private int orderId;
    private String customerId;
    private int productId;
    private int quantity;
    private ORDER_STATUS status;

    public Order(int orderId, String customerId, int productId, int quantity, ORDER_STATUS status) {
        if(orderId < 0 ) throw new IllegalOrderIdException(orderId);
        if(customerId.isEmpty()) throw new IllegalCustomerIdException(customerId);
        if(productId < 0) throw new IllegalProductIdException(productId);
        if(quantity < 0) throw new IllegalQuantityException(quantity);
        
        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
    }

    public int getOrderId() { return orderId; }

    public String getCustomerId() { return customerId; }

    public int getProductId() { return productId; }

    public int getQuantity() { return quantity; }

    public ORDER_STATUS getStatus() { return status; }

}
