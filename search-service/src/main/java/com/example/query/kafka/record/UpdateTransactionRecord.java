package com.example.query.kafka.record;

public record UpdateTransactionRecord(long id, long predictCost, boolean isReliable) implements EventRecord {
    @Override
    public Long getPartitionKey() {
        return id;
    }
}
