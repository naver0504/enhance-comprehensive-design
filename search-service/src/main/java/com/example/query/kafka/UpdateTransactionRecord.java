package com.example.query.kafka;

public record UpdateTransactionRecord(long id, long predictCost, boolean isReliable) {
}
