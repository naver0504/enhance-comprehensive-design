package com.example.query.kafka.record;

public interface EventRecord {
    Long getPartitionKey();
    Pair<Query, Update> toQueryUpdate();
}
