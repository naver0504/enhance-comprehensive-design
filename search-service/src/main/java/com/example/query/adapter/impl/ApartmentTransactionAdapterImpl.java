package com.example.query.adapter.impl;

import com.example.query.adapter.ApartmentTransactionAdapter;
import com.example.query.adapter.document.ApartmentTransaction;
import com.example.query.adapter.order.CustomPageImpl;
import com.example.query.adapter.order.CustomPageable;
import com.example.query.adapter.repository.ApartmentTransactionMongoRepository;
import com.example.query.adapter.repository.QuerydslSearchApartmentTransactionRepository;
import com.example.query.dto.request.SearchCondition;
import com.example.query.dto.response.SearchApartNameResponse;
import com.example.query.dto.response.SearchAreaResponse;
import com.example.query.dto.response.SearchResponseRecord;
import com.example.query.dto.response.TransactionDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApartmentTransactionAdapterImpl implements ApartmentTransactionAdapter {

    private final ApartmentTransactionMongoRepository apartmentTransactionMongoRepository;
    private final QuerydslSearchApartmentTransactionRepository querydslApartmentTransactionRepository;

    @Override
    public boolean isExistTransaction(Long transactionId) {
        return querydslApartmentTransactionRepository.isExistsByTransactionId(transactionId);
    }

    @Override
    public void save(ApartmentTransaction apartmentTransaction) {
        apartmentTransactionMongoRepository.save(apartmentTransaction);
    }

    @Override
    public Optional<ApartmentTransaction> findApartmentTransactionById(Long transactionId) {
        return apartmentTransactionMongoRepository.findByTransactionId(transactionId);
    }

    @Override
    public Page<SearchResponseRecord> searchApartmentTransactions(Long cachedCount, SearchCondition searchCondition, CustomPageable customPageable) {
        List<SearchResponseRecord> contents = querydslApartmentTransactionRepository.searchApartmentTransactions(searchCondition, customPageable)
                .stream().map(SearchResponseRecord::new).toList();

        if(ObjectUtils.isEmpty(cachedCount)) cachedCount = querydslApartmentTransactionRepository.getCount(searchCondition);
        return new CustomPageImpl<>(contents, customPageable.toPageable(), cachedCount);
    }

    @Override
    public List<SearchApartNameResponse> findApartmentNames(String gu, String dongName) {
        return apartmentTransactionMongoRepository.findApartmentNames(gu, dongName)
                .stream().map(SearchApartNameResponse::new).toList();
    }

    @Override
    public List<SearchAreaResponse> findAreaForExclusive(String gu, String dongName, String apartmentName) {
        return apartmentTransactionMongoRepository.findAreaForExclusive(gu, dongName, apartmentName)
                .stream().map(SearchAreaResponse::new).toList();
    }


    @Override
    public List<ApartmentTransaction> findApartmentTransactionsForGraph(String gu, String dongName, String apartmentName, double areaForExclusiveUse, LocalDate startDate, LocalDate endDate) {
        return apartmentTransactionMongoRepository.findApartmentTransactionsForGraph(gu, dongName, apartmentName, areaForExclusiveUse, startDate, endDate);
    }
}
