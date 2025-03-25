package com.example.query.adapter.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Objects;

import static com.example.query.adapter.document.QApartmentTransaction.apartmentTransaction;

public class QuerydslTransactionSupporter extends QuerydslRepositorySupport {

    public QuerydslTransactionSupporter(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    public BooleanExpression eqGu(String gu) {
        return !StringUtils.hasText(gu) ? null : apartmentTransaction.gu.eq(gu);
    }

    public BooleanExpression eqDong(String dong) {
        return !StringUtils.hasText(dong) ? null : apartmentTransaction.dong.eq(dong);
    }

    public BooleanExpression eqApartmentName(String apartmentName) {
        return !StringUtils.hasText(apartmentName) ? null : apartmentTransaction.apartmentName.eq(apartmentName);
    }

    public BooleanExpression eqAreaForExclusiveUse(Double areaForExclusiveUse) {
        return areaForExclusiveUse == null ? null : apartmentTransaction.areaForExclusiveUse.eq(areaForExclusiveUse);
    }

    public BooleanExpression betweenDealDate(LocalDate startDealDate, LocalDate endDealDate) {
        if(startDealDate == null && endDealDate == null) {
            return null;
        }
        startDealDate = Objects.isNull(startDealDate) ? LocalDate.of(2006, 1, 1) : startDealDate;
        endDealDate = Objects.isNull(endDealDate) ? LocalDate.now() : endDealDate;
        return apartmentTransaction.dealDate.between(startDealDate, endDealDate);
    }
}
