package com.example.query.kafka.record;

public record UpdateTransactionRecord(long id, long predictCost, boolean isReliable) implements EventRecord {
    @Override
    public Long getPartitionKey() {
        return id;
    }

    @Override
    public Pair<Query, Update> toQueryUpdate() {
        Query query = new Query();
        query.addCriteria(where("transactionId").is(id));

        Update update = new Update();
        update.set("predictedCost", predictCost);
        update.set("isReliable", isReliable);

        return Pair.of(query, update);
    }
}
