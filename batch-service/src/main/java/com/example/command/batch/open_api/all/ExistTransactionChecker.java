package com.example.command.batch.open_api.all;

import com.example.command.adapter.repository.apart.QuerydslApartmentTransaction;
import com.example.command.batch.open_api.dto.AlreadyExistTransactioRecord;
import com.example.command.batch.open_api.dto.ApartmentDetail;
import com.example.command.domain.dong.Gu;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.Map;

@RequiredArgsConstructor
public class ExistTransactionChecker {

    private final QuerydslApartmentTransaction querydslApartmentTransaction;
    private Map<Gu, AlreadyExistTransactioRecord> lastTransactionMap;

    @Value("#{jobParameters[contractDate]}")
    private LocalDate contractDate;

    @PostConstruct
    public void init() {
        lastTransactionMap = querydslApartmentTransaction.findLastTransactionByDealDate(LocalDate.of(contractDate.getYear(), contractDate.getMonth(), 1));
    }

    public boolean isExistTransaction(Gu gu, ApartmentDetail apartmentDetail) {
        AlreadyExistTransactioRecord lastTransaction = lastTransactionMap.get(gu);
        return lastTransaction == null || lastTransaction.equals(apartmentDetail);
    }
}
