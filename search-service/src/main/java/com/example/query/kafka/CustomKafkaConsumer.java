package com.example.query.kafka;

import com.example.query.adapter.ApartmentTransactionAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class CustomKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final ApartmentTransactionAdapter apartmentTransactionAdapter;

    @KafkaListener(topics = CustomKafkaProperties.CREATE_TRANSACTION_TOPIC, groupId = "query-service", containerFactory = "kafkaListenerContainerFactory")
    public void consumeCreateTransactionEvent(@Payload String message) {
        try {
            CreateTransactionRecord createTransactionRecord = objectMapper.readValue(message, CreateTransactionRecord.class);
            if(apartmentTransactionAdapter.isExistTransaction(createTransactionRecord.id())) {
                log.info("Transaction record already exists: {}", createTransactionRecord.id());
                return;
            }
            apartmentTransactionAdapter.save(createTransactionRecord.toEntity());
        } catch (Exception e) {
            log.error("Failed to parse message: {}", message, e);
        }
    }
}
