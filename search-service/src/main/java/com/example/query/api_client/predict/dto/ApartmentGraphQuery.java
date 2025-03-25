package com.example.query.api_client.predict.dto;


import com.example.query.adapter.document.ApartmentTransaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApartmentGraphQuery implements ApartmentQuery {

    public ApartmentGraphQuery(ApartmentTransaction apartmentTransaction) {
        this(
                apartmentTransaction.getGu(),
                apartmentTransaction.getDong(),
                apartmentTransaction.getAreaForExclusiveUse(),
                apartmentTransaction.getFloor(),
                apartmentTransaction.getBuildYear()
        );
    }

    private String gu;
    private String dongName;
    private double areaForExclusiveUse;
    private int floor;
    private int buildYear;
}
