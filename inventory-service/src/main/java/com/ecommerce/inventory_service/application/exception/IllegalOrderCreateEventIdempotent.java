package com.ecommerce.inventory_service.application.exception;

public class IllegalOrderCreateEventIdempotent extends RuntimeException {
    private int eventId;

    public IllegalOrderCreateEventIdempotent(int eventId) {
        this.eventId = eventId;
    }

    public int getEventId() { return this.eventId;}
}
