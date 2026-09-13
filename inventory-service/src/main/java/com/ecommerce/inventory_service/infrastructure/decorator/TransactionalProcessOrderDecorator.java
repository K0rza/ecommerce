package com.ecommerce.inventory_service.infrastructure.decorator;

import com.ecommerce.inventory_service.application.port.in.ProcessOrderUseCase;
import com.ecommerce.inventory_service.domain.value.OrderRecord;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
public class TransactionalProcessOrderDecorator implements ProcessOrderUseCase {
    
    private final ProcessOrderUseCase useCase;

    @Override
    @Transactional
    public void process(OrderRecord orderRecord) {
       useCase.process(orderRecord);
    }

}
