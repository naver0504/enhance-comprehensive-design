package com.example.query.adapter.order;

import com.querydsl.core.types.dsl.ComparableExpressionBase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.example.query.adapter.document.QApartmentTransaction.apartmentTransaction;


@Getter
@RequiredArgsConstructor
public enum OrderType {

    DEAL_AMOUNT(apartmentTransaction.dealAmount),
    AREA_FOR_EXCLUSIVE_USE(apartmentTransaction.areaForExclusiveUse),
    DEAL_DATE(apartmentTransaction.dealDate);

    private final ComparableExpressionBase<? extends Comparable> comparableExpressionBase;
}
