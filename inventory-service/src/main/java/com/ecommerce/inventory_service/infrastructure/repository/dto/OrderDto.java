package com.ecommerce.inventory_service.infrastructure.repository.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity  
@NoArgsConstructor 
@Table(name = "processed_order_event")
public class OrderDto {

    @Id
    private int eventId;

    public int getEventId() { return eventId; }

    public OrderDto(int eventId) { this.eventId = eventId; }    
}
