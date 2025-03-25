package com.example.query.dto.response;


import com.example.query.adapter.document.ApartmentTransaction;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record RealTransactionGraphResponse(Map<String, List<Integer>> realData) {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    public RealTransactionGraphResponse(List<ApartmentTransaction> transactions) {
        this(transactions.stream()
                .collect(
                        Collectors.groupingBy(
                                apartmentTransaction -> YearMonth.from(apartmentTransaction.getDealDate()).format(FORMATTER),
                                Collectors.mapping(ApartmentTransaction::getDealAmount, Collectors.toList())
                        )));
    }
}
