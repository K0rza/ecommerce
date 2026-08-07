package com.ecommerce.product_service.infrastructure.scheduler;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecommerce.product_service.infrastructure.persistence.adapter.JpaOutboxRepositoryAdapter;
import com.ecommerce.product_service.infrastructure.persistence.entity.OutboxEventEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final JpaOutboxRepositoryAdapter outboxAdapter;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    @Scheduled(fixedDelay = 1000)
    public void outboxJob() {
        outboxAdapter.findNotProcessed()
            .forEach(this::send);
    }

    private void send(OutboxEventEntity event) {
        log.error("sending data to kafka and processed");

        try {
            kafkaTemplate.send(event.getEventName(), mapper.writeValueAsString(event))
                .thenRun(() -> {
                    outboxAdapter.processed(event.getEventId());
                    log.error("sent data: " + event.getPayload() + " to topic: " + event.getEventName() + " lastStaus: " + event.isProcessed());
                });
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

}
