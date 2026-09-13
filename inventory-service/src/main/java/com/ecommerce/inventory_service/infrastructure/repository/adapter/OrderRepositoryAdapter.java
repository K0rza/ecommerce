package com.ecommerce.inventory_service.infrastructure.repository.adapter;

import java.util.function.IntConsumer;

import org.springframework.stereotype.Component;

import com.ecommerce.inventory_service.application.port.out.OrderRepositoryPort;
import com.ecommerce.inventory_service.infrastructure.repository.dto.OrderDto;
import com.ecommerce.inventory_service.infrastructure.repository.jpaRepositories.JpaOrderInterface;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final JpaOrderInterface jpaInterface;

    @Override
    public void ifNewOrderOrElse(int eventId, Runnable newOrderJob, IntConsumer rejectOrderJob) {
        jpaInterface
            .findById(eventId)
            .ifPresentOrElse(
                _ -> rejectOrderJob.accept(eventId),
                () -> newOrderJob.run());
    }

    @Transactional 
    @Override
    public void eventConsumed(int eventId) {
        jpaInterface.save(new OrderDto(eventId));
    }

}
