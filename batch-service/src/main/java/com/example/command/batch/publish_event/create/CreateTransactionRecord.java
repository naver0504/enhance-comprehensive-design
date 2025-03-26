package com.example.command.batch.publish_event.create;

import com.example.command.batch.publish_event.EventItemRecord;
import com.example.command.domain.apartment.ApartmentTransaction;
import com.example.command.domain.apartment.DealingGbn;
import com.example.command.domain.dong.DongEntity;
import com.example.command.domain.dong.Gu;
import com.example.command.domain.predict_cost.PredictCost;

import java.time.LocalDate;

public record CreateTransactionRecord(long id, String apartmentName, int buildYear, int dealAmount,
                                      double areaForExclusiveUse, String jibun, int floor, LocalDate dealDate, DealingGbn dealingGbn,
                                      Double latitude, Double longitude, Gu gu, String dong, long predictedCost, boolean isReliable
                                      ) implements EventItemRecord {

    public CreateTransactionRecord(long transactionId, ApartmentTransaction apartmentTransaction, DongEntity dongEntity, PredictCost predictCost) {
        this(transactionId, apartmentTransaction.getApartmentName(), apartmentTransaction.getBuildYear(), apartmentTransaction.getDealAmount(),
                apartmentTransaction.getAreaForExclusiveUse(), apartmentTransaction.getJibun(), apartmentTransaction.getFloor(), apartmentTransaction.getDealDate(), apartmentTransaction.getDealingGbn(),
                apartmentTransaction.getGeography() != null ? apartmentTransaction.getGeography().getX() : null, apartmentTransaction.getGeography() != null ? apartmentTransaction.getGeography().getY() : null,
                dongEntity.getGu(), dongEntity.getDongName(), predictCost.getPredictedCost(), predictCost.isReliable());
    }

    @Override
    public Long getPartitionKey() {
        return id;
    }
}
