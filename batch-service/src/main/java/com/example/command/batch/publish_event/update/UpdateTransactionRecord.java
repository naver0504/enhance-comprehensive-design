package com.example.command.batch.publish_event.update;

import com.example.command.batch.publish_event.EventItemRecord;

public record UpdateTransactionRecord(long id, long predictCost, boolean isReliable) implements EventItemRecord {

    @Override
    public Long getPartitionKey() {
        return id;
    }
}
