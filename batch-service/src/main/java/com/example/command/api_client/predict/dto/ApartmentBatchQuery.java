package com.example.command.api_client.predict.dto;

import com.example.command.domain.dong.Gu;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ApartmentBatchQuery implements ApartmentQuery {
    private long id;
    private double interestRate;
    private Gu gu;
    private String dongName;
    private LocalDate dealDate;
    private int dealAmount;
    private double areaForExclusiveUse;
    private int floor;
    private int buildYear;
}
