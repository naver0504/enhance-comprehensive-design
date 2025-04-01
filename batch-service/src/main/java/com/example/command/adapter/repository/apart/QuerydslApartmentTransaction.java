package com.example.command.adapter.repository.apart;

import com.example.command.batch.open_api.dto.ApartmentDetail;
import com.example.command.domain.apartment.ApartmentTransaction;
import com.example.command.domain.dong.Gu;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.command.domain.apartment.QApartmentTransaction.apartmentTransaction;
import static com.example.command.domain.dong.QDongEntity.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

@RequiredArgsConstructor
@Repository
public class QuerydslApartmentTransaction {

    private final JPAQueryFactory queryFactory;

    public Map<Gu, Set<ApartmentDetail>> findTransactionByContractDate(LocalDate contractDate) {
        LocalDate startOfMonth = LocalDate.of(contractDate.getYear(), contractDate.getMonth(), 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1);

        List<ApartmentTransaction> apartmentTransactions = queryFactory.select(apartmentTransaction)
                .from(apartmentTransaction)
                .innerJoin(dongEntity).fetchJoin()
                .on(apartmentTransaction.dongEntity.id.eq(dongEntity.id))
                .where(
                        apartmentTransaction.dealDate.goe(startOfMonth),
                        apartmentTransaction.dealDate.lt(endOfMonth)
                )
                .fetch();

        if(apartmentTransactions.isEmpty()) return Collections.emptyMap();

        return apartmentTransactions.stream()
                .collect(groupingBy(ApartmentTransaction::getGu, mapping(ApartmentDetail::new, Collectors.toSet())));
    }
}
