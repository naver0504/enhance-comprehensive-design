package com.example.query.kafka;

public record UpdateTransactionRecord(long transactionId, long predictCost, boolean isReliable) {
}
