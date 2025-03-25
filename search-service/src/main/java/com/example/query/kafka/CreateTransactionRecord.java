package com.example.query.kafka;

import com.example.query.adapter.document.ApartmentTransaction;

import java.time.LocalDate;

public record CreateTransactionRecord(long id, String apartmentName, int buildYear, int dealAmount,
                                      double areaForExclusiveUse, String jibun, int floor, LocalDate dealDate, String dealingGbn,
                                      Double latitude, Double longitude, String gu, String dong, long predictedCost, boolean isReliable
                                      ) {

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
}