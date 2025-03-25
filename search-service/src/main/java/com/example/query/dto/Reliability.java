package com.example.query.dto;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;

import static com.example.query.adapter.document.QApartmentTransaction.*;

@Getter
public enum Reliability {

    ALL(null),
    RELIABLE(apartmentTransaction.isReliable.isTrue()),
    UNRELIABLE(apartmentTransaction.isReliable.isFalse());

    private final BooleanExpression reliabilityExpression;

    Reliability(BooleanExpression reliabilityExpression) {
            this.reliabilityExpression = reliabilityExpression;
        }
}