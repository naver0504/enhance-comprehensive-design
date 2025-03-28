package com.example.command.kafka.config;

import com.example.command.batch.publish_event.EventItemRecord;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<Long, EventItemRecord> producerFactory() {
        Map<String, Object> producerProperties = new HashMap<>();
        producerProperties.put("bootstrap.servers", kafkaProperties.getBootstrapServers());
        producerProperties.put("key.serializer", LongSerializer.class);
        producerProperties.put("value.serializer", JsonSerializer.class);
        producerProperties.put("auto.offset.reset", "earliest");
        producerProperties.put("acks", "all");

        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    public KafkaTemplate<Long, EventItemRecord> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
