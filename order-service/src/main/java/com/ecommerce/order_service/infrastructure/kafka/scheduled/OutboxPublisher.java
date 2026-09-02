package com.ecommerce.order_service.infrastructure.kafka.scheduled;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecommerce.order_service.infrastructure.persistence.adapter.OrderOutboxJpaRepository;
import com.ecommerce.order_service.infrastructure.persistence.dto.OrderOutboxDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OutboxPublisher {

    private final OrderOutboxJpaRepository orderOutboxRepository;
    private final ObjectMapper mapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 1000)
    public void publish() {
        orderOutboxRepository.findAll().stream().filter(item -> item.isNotPublished()).forEach(this::sendToKafka);
    }

    private void sendToKafka(OrderOutboxDto dto) {
        try {
            kafkaTemplate.send("order-created", mapper.writeValueAsString(dto.toKafkaEvent()))
                .thenRun(() -> {
                    dto.published();
                    orderOutboxRepository.save(dto);
                }
            );
        } catch (JsonProcessingException e) {
           System.err.print(e);
        }
    }
}
