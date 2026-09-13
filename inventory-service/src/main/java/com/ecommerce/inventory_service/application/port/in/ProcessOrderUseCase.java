package com.ecommerce.inventory_service.application.port.in;

import com.ecommerce.inventory_service.domain.value.OrderRecord;

public interface ProcessOrderUseCase {

    void process(OrderRecord orderRecord);
}
