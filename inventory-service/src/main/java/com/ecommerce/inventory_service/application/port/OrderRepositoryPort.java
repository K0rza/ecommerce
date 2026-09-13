package com.ecommerce.inventory_service.application.port;

import java.util.function.IntConsumer;

public interface OrderRepositoryPort {

    void ifNewOrderOrElse(int eventId, Runnable newOrderJob, IntConsumer rejectOrderJob);

    void eventConsumed(int eventId);

}
