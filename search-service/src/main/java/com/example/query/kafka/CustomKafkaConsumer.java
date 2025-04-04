package com.example.query.kafka;

import com.example.query.adapter.document.ApartmentTransaction;
import com.example.query.kafka.record.CreateTransactionRecord;
import com.example.query.kafka.record.EventRecord;
import com.example.query.kafka.record.UpdateTransactionRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Component
@Slf4j
public class CustomKafkaConsumer {

    private final MongoTemplate mongoTemplate;
    private final ForkJoinPool kafkaForkJoinPool;

    public CustomKafkaConsumer(MongoTemplate mongoTemplate, @Qualifier(value = "kafkaForkJoinPool") ForkJoinPool kafkaForkJoinPool) {
        this.mongoTemplate = mongoTemplate;
        this.kafkaForkJoinPool = kafkaForkJoinPool;
    }


    @KafkaListener(topics = CustomKafkaProperties.CREATE_TRANSACTION_TOPIC, groupId = "query-service", containerFactory = "batchKafkaListenerContainerFactoryForCreateEvent")
    public void consumeCreateTransactionEvent(@Payload List<CreateTransactionRecord> messages)  {

        List<Pair<Query, Update>> pairs = kafkaForkJoinPool.submit(() ->
                messages.parallelStream()
                        .map(EventRecord::toQueryUpdate)
                        .toList()
        ).join();

        mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, ApartmentTransaction.class)
                .upsert(pairs)
                .execute();
    }

    @KafkaListener(topics = CustomKafkaProperties.UPDATE_TRANSACTION_TOPIC, groupId = "query-service", containerFactory = "batchKafkaListenerContainerFactoryForUpdateEvent")
    public void consumeUpdateTransactionEvent(@Payload List<UpdateTransactionRecord> messages)  {
        List<Pair<Query, Update>> pairs = kafkaForkJoinPool.submit(() ->
                messages.parallelStream()
                        .map(EventRecord::toQueryUpdate)
                        .toList()
        ).join();

        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, ApartmentTransaction.class);

        for (Pair<Query, Update> pair : pairs) {
            bulkOperations.updateOne(pair.getFirst(), pair.getSecond());
        }

        bulkOperations.execute();
    }
}
