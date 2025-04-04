package com.example.query.kafka;

import com.example.query.kafka.record.CreateTransactionRecord;
import com.example.query.kafka.record.EventRecord;
import com.example.query.kafka.record.UpdateTransactionRecord;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@EnableKafka
@RequiredArgsConstructor
@EnableConfigurationProperties(CustomKafkaProperties.class)
@Profile("!test")
@Configuration
public class KafkaConsumerConfiguration {

    private final CustomKafkaProperties kafkaProperties;

    @Bean(name = "kafkaForkJoinPool")
    public ForkJoinPool kafkaForkJoinPool() {
        return new ForkJoinPool(7);
    }

    @Bean(name = "batchKafkaListenerContainerFactoryForCreateEvent")
    public ConcurrentKafkaListenerContainerFactory<Long, CreateTransactionRecord> batchKafkaListenerContainerFactoryForCreateEvent() {
        ConcurrentKafkaListenerContainerFactory<Long, CreateTransactionRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
        ContainerProperties containerProperties = factory.getContainerProperties();
        containerProperties.setIdleBetweenPolls(500);

        factory.setConsumerFactory(consumerFactory(CreateTransactionRecord.class));
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        return factory;
    }

    @Bean(name = "batchKafkaListenerContainerFactoryForUpdateEvent")
    public ConcurrentKafkaListenerContainerFactory<Long, UpdateTransactionRecord> batchKafkaListenerContainerFactoryForUpdateEvent() {
        ConcurrentKafkaListenerContainerFactory<Long, UpdateTransactionRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
        ContainerProperties containerProperties = factory.getContainerProperties();
        containerProperties.setIdleBetweenPolls(500);

        factory.setBatchListener(true);
        factory.setConsumerFactory(consumerFactory(UpdateTransactionRecord.class));
        factory.setConcurrency(1);
        return factory;
    }

    private <T extends EventRecord> ConsumerFactory<Long, T> consumerFactory(Class<T> eventRecordClass) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "query-service");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 200000);
        properties.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 9000);
        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024 * 1024);
        properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 1000);
        return new DefaultKafkaConsumerFactory<>(properties, new LongDeserializer(), new JsonDeserializer<>(eventRecordClass, false));
    }

}
