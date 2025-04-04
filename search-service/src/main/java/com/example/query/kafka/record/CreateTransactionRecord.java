package com.example.query.kafka.record;

import com.example.query.adapter.document.ApartmentTransaction;

import java.time.LocalDate;

public record CreateTransactionRecord(long id, String apartmentName, int buildYear, int dealAmount,
                                      double areaForExclusiveUse, String jibun, int floor, LocalDate dealDate, String dealingGbn,
                                      Double latitude, Double longitude, String gu, String dong, long predictedCost, boolean isReliable
                                      ) implements EventRecord {

    public ApartmentTransaction toEntity() {
        return ApartmentTransaction.builder()
                .transactionId(id)
                .apartmentName(apartmentName)
                .buildYear(buildYear)
                .dealAmount(dealAmount)
                .areaForExclusiveUse(areaForExclusiveUse)
                .jibun(jibun)
                .floor(floor)
                .dealDate(dealDate)
                .dealingGbn(dealingGbn)
                .latitude(latitude)
                .longitude(longitude)
                .gu(gu)
                .dong(dong)
                .predictedCost(predictedCost)
                .isReliable(isReliable)
                .build();
    }

    @Override
    public Long getPartitionKey() {
        return id;
    }

    @Override
    public Pair<Query, Update> toQueryUpdate() {
        Query query = new Query();
        query.addCriteria(where("transactionId").is(id));

        Update update = new Update();
        update.set("apartmentName", apartmentName);
        update.set("buildYear", buildYear);
        update.set("dealAmount", dealAmount);
        update.set("areaForExclusiveUse", areaForExclusiveUse);
        update.set("jibun", jibun);
        update.set("floor", floor);
        update.set("dealDate", dealDate);
        update.set("dealingGbn", dealingGbn);
        update.set("latitude", latitude);
        update.set("longitude", longitude);
        update.set("gu", gu);
        update.set("dong", dong);
        update.set("predictedCost", predictedCost);
        update.set("isReliable", isReliable);

        return Pair.of(query, update);
    }
}