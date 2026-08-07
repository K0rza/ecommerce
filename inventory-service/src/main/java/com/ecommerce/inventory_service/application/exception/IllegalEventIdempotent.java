package com.ecommerce.inventory_service.application.exception;

public class IllegalEventIdempotent extends RuntimeException {
    private int eventId;

    public IllegalEventIdempotent(int eventId) {
        this.eventId = eventId;
    }

    public int getEventId() { return this.eventId;}
}
