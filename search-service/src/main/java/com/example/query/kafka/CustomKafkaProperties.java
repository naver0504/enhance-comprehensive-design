package com.example.query.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record CustomKafkaProperties(String host, int port) {

    public static final String CREATE_TRANSACTION_TOPIC = "create-transaction-event";
    public static final String UPDATE_TRANSACTION_TOPIC = "update-transaction-event";

    public String getBootstrapServers() {
        return host + ":" + port;
    }
}
