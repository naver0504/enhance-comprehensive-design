package com.example.command.api_client.predict.dto;

import com.example.command.domain.apartment.ApartmentTransaction;
import com.example.command.domain.dong.Gu;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApartmentGraphQuery implements ApartmentQuery {

    public ApartmentGraphQuery(ApartmentTransaction apartmentTransaction) {
        this(
                apartmentTransaction.getDongEntity().getGu(),
                apartmentTransaction.getDongEntity().getDongName(),
                apartmentTransaction.getAreaForExclusiveUse(),
                apartmentTransaction.getFloor(),
                apartmentTransaction.getBuildYear()
        );
    }

    private Gu gu;
    private String dongName;
    private double areaForExclusiveUse;
    private int floor;
    private int buildYear;
}
