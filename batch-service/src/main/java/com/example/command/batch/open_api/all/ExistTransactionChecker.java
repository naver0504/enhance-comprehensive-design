package com.example.command.batch.open_api.all;

import com.example.command.adapter.repository.apart.QuerydslApartmentTransaction;
import com.example.command.batch.open_api.PostConstructInitiationBean;
import com.example.command.batch.open_api.dto.ApartmentDetail;
import com.example.command.domain.dong.Gu;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class ExistTransactionChecker implements PostConstructInitiationBean {

    private final QuerydslApartmentTransaction querydslApartmentTransaction;
    private Map<Gu, Set<ApartmentDetail>> contractMonthTransactionMap;

    @Value("#{jobParameters[contractDate]}")
    private LocalDate contractDate;

    @Override
    @PostConstruct
    public void init() {
        contractMonthTransactionMap = Collections.unmodifiableMap(querydslApartmentTransaction.findTransactionByContractDate(contractDate));
    }

    public boolean isNotExistTransaction(Gu gu, ApartmentDetail apartmentDetail) {
        if(contractMonthTransactionMap.isEmpty()) return true;
        return !contractMonthTransactionMap.get(gu).contains(apartmentDetail);
    }
}
