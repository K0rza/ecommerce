package com.ecommerce.inventory_service.infrastructure.decorator;

import com.ecommerce.inventory_service.application.exception.StockUpdateConflictException;
import com.ecommerce.inventory_service.application.port.in.ProcessOrderUseCase;
import com.ecommerce.inventory_service.domain.value.OrderRecord;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
public class RetryingProcessOrderDecorator implements ProcessOrderUseCase {

    private static final int MAX_ATTEMPTS = 3;

    private final ProcessOrderUseCase delegate;

    @Override
    public void process(OrderRecord orderRecord) {
        for(int attempt = 1; attempt <= MAX_ATTEMPTS ; attempt++) {
            try {
                delegate.process(orderRecord);
                return;
            } catch (StockUpdateConflictException e) {
                if(attempt == MAX_ATTEMPTS) throw e;
            }
        }
    }

}
