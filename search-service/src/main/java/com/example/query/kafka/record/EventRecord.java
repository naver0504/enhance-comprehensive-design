package com.example.query.kafka.record;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;

public interface EventRecord {
    Long getPartitionKey();
    Pair<Query, Update> toQueryUpdate();
}
