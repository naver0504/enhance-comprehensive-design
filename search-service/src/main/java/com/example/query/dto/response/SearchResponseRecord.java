package com.example.query.dto.response;


import com.example.query.adapter.document.ApartmentTransaction;

import java.time.LocalDate;
public record SearchResponseRecord(
        long transactionId,
        String apartmentName,
        String region,
        double areaForExclusiveUse,
        LocalDate dealDate,
        int dealAmount,
        long predictedCost,
        boolean isReliable
) {

    public SearchResponseRecord(ApartmentTransaction apartmentTransaction) {
        this(apartmentTransaction.getTransactionId(), apartmentTransaction.getApartmentName(), createRegion(apartmentTransaction.getGu(), apartmentTransaction.getDong()),
                apartmentTransaction.getAreaForExclusiveUse(), apartmentTransaction.getDealDate(),
                apartmentTransaction.getDealAmount(), apartmentTransaction.getPredictedCost(), apartmentTransaction.isReliable());
    }

    public static String createRegion(String gu, String dongName) {
        return gu + " " + dongName;
    }

}
