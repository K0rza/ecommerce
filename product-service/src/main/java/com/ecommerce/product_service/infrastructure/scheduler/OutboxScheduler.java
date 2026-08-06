package com.ecommerce.product_service.infrastructure.scheduler;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecommerce.product_service.infrastructure.persistence.adapter.JpaOutboxRepositoryAdapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final JpaOutboxRepositoryAdapter outboxAdapter;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    public void outboxJob() {
        outboxAdapter.findNotProcessed()
            .forEach(event -> {
                log.error("sending data to kafka and processed");
                kafkaTemplate.send(event.getEventName(), event.getPayload())
                    .thenRun(() -> {
                        outboxAdapter.processed(event.getId());
                        log.error("sent data: " + event.getPayload() + " to topic: " + event.getEventName() + " lastStaus: " + event.isProcessed());
                    });
            });
    }

}
