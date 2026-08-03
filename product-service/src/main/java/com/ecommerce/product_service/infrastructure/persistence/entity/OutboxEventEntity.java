package com.ecommerce.product_service.infrastructure.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "OutboxEventEntity")
public class OutboxEventEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private int productId;
    private String eventName;
    private String payload;
    private boolean processed;

    private OutboxEventEntity(int productId, String eventName, String payload) {
        this.productId = productId;
        this.eventName = eventName;
        this.payload = payload;
    }

    public static OutboxEventEntity of(int productId, String eventName, String payload) {
        return new OutboxEventEntity(productId, eventName, payload);
    }

    @Override
    public String toString() {
        return "OutboxEventEntity [id=" + id + ", productId=" + productId + ", eventName=" + eventName + ", payload="
                + payload + ", processed=" + processed + "]";
    }
}
