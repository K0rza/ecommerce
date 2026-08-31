package com.ecommerce.inventory_service.domain.entity;

import com.ecommerce.inventory_service.application.exception.OutOfStockException;
import com.ecommerce.inventory_service.domain.value.OrderRecord;

public class Inventory {
    private int productId;
    private int orderId; 
    private int quantity; 
    private int stock;

    public Inventory(int productId, int orderId, int quantity, int stock) { 
       if(quantity > stock) 
            throw new OutOfStockException(orderId);

       this.orderId = orderId;
       this.productId = productId;
       this.quantity = quantity;
       this.stock = stock;
    }

    public static Inventory of(OrderRecord orderEvent, int stock) {
        return new Inventory(orderEvent.productId(), orderEvent.orderId(), orderEvent.quantity(), stock);
    }

    public void decreaseStock() { stock -= quantity; }

    public int getProductId() { return productId; }

    public int getOrderId() { return orderId; }

    public int getQuantity() { return quantity; }

    public int getStock() { return stock; }

}
