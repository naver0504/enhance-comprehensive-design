package com.example.command.batch.publish_event.update;

public record UpdateTransactioRecord(long transactionId, long predictCost, boolean isReliable) {
}
