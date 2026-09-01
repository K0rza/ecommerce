package com.ecommerce.order_service.infrastructure.kafka.scheduled;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecommerce.order_service.infrastructure.persistence.adapter.JpaOrderOutboxTableAdapter;
import com.ecommerce.order_service.infrastructure.persistence.dto.OrderOutboxTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OutboxPublisher {

    private final JpaOrderOutboxTableAdapter jpaAdapter;
    private final ObjectMapper mapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 1000)
    public void publish() {
        jpaAdapter.findAll().stream().filter(item -> item.isNotPublished()).forEach(this::sendToKafka);
    }

    private void sendToKafka(OrderOutboxTable dto) {
        try {
            kafkaTemplate.send("order-created", mapper.writeValueAsString(dto.toKafkaEvent())).thenRun(() -> dto.published());
        } catch (JsonProcessingException e) {
           System.err.print(e);
        }
    }
}
