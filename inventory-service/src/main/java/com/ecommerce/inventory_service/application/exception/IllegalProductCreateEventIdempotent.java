package com.ecommerce.inventory_service.application.exception;

public class IllegalProductCreateEventIdempotent extends RuntimeException {
    private int eventId;

    public IllegalProductCreateEventIdempotent(int eventId) {
        this.eventId = eventId;
    }

    public int getEventId() { return this.eventId;}
}
