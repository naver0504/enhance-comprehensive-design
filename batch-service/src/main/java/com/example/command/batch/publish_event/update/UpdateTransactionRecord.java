package com.example.command.batch.publish_event.update;

public record UpdateTransactionRecord(long transactionId, long predictCost, boolean isReliable) {
}
