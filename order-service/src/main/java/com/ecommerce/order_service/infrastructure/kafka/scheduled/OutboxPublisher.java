package com.ecommerce.order_service.infrastructure.kafka.scheduled;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecommerce.order_service.infrastructure.persistence.adapter.OrderOutboxJpaRepository;
import com.ecommerce.order_service.infrastructure.persistence.dto.OrderOutboxDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Component
@Slf4j
public class OutboxPublisher {

    private final OrderOutboxJpaRepository orderOutboxRepository;
    private final ObjectMapper mapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 1000)
    public void publish() {
        orderOutboxRepository
            .findByPublishedFalse()
            .forEach(this::sendToKafka);
    }

    private void sendToKafka(OrderOutboxDto dto) {
        try {
            kafkaTemplate.send("order-created", mapper.writeValueAsString(dto.toKafkaEvent()))
                .thenRun(() -> {
                    dto.published();
                    orderOutboxRepository.save(dto);
                    log.debug("%s::sendToKafka outbox event sent to kafka.".formatted(this.getClass().getSimpleName(), dto));
                }
            );
        } catch (JacksonException e) {
           log.error(e.getMessage());
        }
    }
}
