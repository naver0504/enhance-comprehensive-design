package com.example.command.adapter.repository.apart;

import com.example.command.batch.open_api.dto.AlreadyExistTransactioRecord;
import com.example.command.domain.dong.Gu;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.command.batch.open_api.dto.AlreadyExistTransactioRecord.*;
import static com.example.command.domain.apartment.QApartmentTransaction.apartmentTransaction;
import static com.example.command.domain.dong.QDongEntity.*;

@RequiredArgsConstructor
@Repository
public class QuerydslApartmentTransaction {

    private final JPAQueryFactory queryFactory;

    public Map<Gu, AlreadyExistTransactioRecord> findLastTransactionByDealDate(LocalDate startDate) {
        List<Long> maxIds = queryFactory.select(apartmentTransaction.id.min())
                .from(apartmentTransaction)
                .innerJoin(dongEntity)
                .on(apartmentTransaction.dongEntity.id.eq(dongEntity.id))
                .where(apartmentTransaction.dealDate.goe(startDate))
                .groupBy(dongEntity.gu)
                .fetch();

        System.out.println("maxIds = " + maxIds);

        List<AlreadyExistTransactionRecordWithGu> records = queryFactory.select(Projections.constructor(AlreadyExistTransactionRecordWithGu.class,
                        dongEntity.gu,
                        apartmentTransaction.dealDate,
                        apartmentTransaction.jibun,
                        apartmentTransaction.apartmentName,
                        apartmentTransaction.floor,
                        apartmentTransaction.buildYear,
                        apartmentTransaction.areaForExclusiveUse,
                        apartmentTransaction.dealAmount
                ))
                .from(apartmentTransaction)
                .innerJoin(dongEntity)
                .on(apartmentTransaction.dongEntity.id.eq(dongEntity.id))
                .where(apartmentTransaction.id.in(maxIds))
                .fetch();
        System.out.println("records = " + records);
        return records
                .stream()
                .collect(
                        Collectors.toMap(
                                AlreadyExistTransactionRecordWithGu::gu,
                                AlreadyExistTransactioRecord::new
                        )
                );
    }

}
