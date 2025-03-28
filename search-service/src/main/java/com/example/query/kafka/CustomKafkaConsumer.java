package com.example.query.kafka;

import com.example.query.adapter.ApartmentTransactionAdapter;
import com.example.query.kafka.record.CreateTransactionRecord;
import com.example.query.kafka.record.EventRecord;
import com.example.query.kafka.record.UpdateTransactionRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public void consumeCreateTransactionEvent(@Payload String message) throws JsonProcessingException {
        CreateTransactionRecord createTransactionRecord = convertToEventRecord(message, CreateTransactionRecord.class);
        if (apartmentTransactionAdapter.isExistTransaction(createTransactionRecord.id())) {
            return;
        }
        apartmentTransactionAdapter.save(createTransactionRecord.toEntity());

    }

    @KafkaListener(topics = CustomKafkaProperties.UPDATE_TRANSACTION_TOPIC, groupId = "query-service", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUpdateTransactionEvent(@Payload String message) throws JsonProcessingException {
        log.info("consumeUpdateTransactionEvent message: {}", message);
        UpdateTransactionRecord updateTransactionRecord = convertToEventRecord(message, UpdateTransactionRecord.class);
        apartmentTransactionAdapter.updatePredictCost(updateTransactionRecord.id(), updateTransactionRecord.predictCost(), updateTransactionRecord.isReliable());
    }

    public <T extends EventRecord> T convertToEventRecord(String message, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(message, clazz);
    }
}
