package com.example.query.dto.response;

import com.example.query.adapter.document.ApartmentTransaction;

import java.time.LocalDate;

public record TransactionDetailResponse(LocalDate dealDate, int buildYear, double areaForExclusiveUse, String dealingGbn,
                                        String apartmentName, int dealAmount, long predictCost, Double latitude, Double longitude) {

    public TransactionDetailResponse(ApartmentTransaction apartmentTransaction) {
        this(apartmentTransaction.getDealDate(), apartmentTransaction.getBuildYear(), apartmentTransaction.getAreaForExclusiveUse(),
                apartmentTransaction.getDealingGbn(), apartmentTransaction.getApartmentName(), apartmentTransaction.getDealAmount(),
                apartmentTransaction.getPredictedCost(), apartmentTransaction.getLatitude(), apartmentTransaction.getLongitude());
    }
}
