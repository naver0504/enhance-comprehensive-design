package com.example.command.batch.excel_write.dto;

import com.example.command.domain.dong.Gu;

import java.time.LocalDate;

public record ExcelOutputRecord (
        LocalDate dealDate,
        double interestRate,
        Gu gu,
        String dong,
        double exclusiveArea,
        int floor,
        int buildYear,
        int dealAmount,
        String apartmentName,
        String jibunAddress
) {

}
