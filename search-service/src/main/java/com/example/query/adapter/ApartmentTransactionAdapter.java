package com.example.query.adapter;

import com.example.query.adapter.document.ApartmentTransaction;
import com.example.query.adapter.order.CustomPageable;
import com.example.query.dto.request.SearchCondition;
import com.example.query.dto.response.SearchApartNameResponse;
import com.example.query.dto.response.SearchAreaResponse;
import com.example.query.dto.response.SearchResponseRecord;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ApartmentTransactionAdapter {

    boolean isExistTransaction(Long transactionId);
    void save(ApartmentTransaction apartmentTransaction);
    Optional<ApartmentTransaction> findApartmentTransactionById(Long id);
    Page<SearchResponseRecord> searchApartmentTransactions(Long cachedCount, SearchCondition searchCondition, CustomPageable customPageable);
    List<SearchApartNameResponse> findApartmentNames(String gu, String dongName);
    List<SearchAreaResponse> findAreaForExclusive(String gu, String dongName, String apartmentName);
    public List<ApartmentTransaction> findApartmentTransactionsForGraph(String gu, String dongName, String apartmentName, double areaForExclusiveUse, LocalDate startDate, LocalDate endDate);
    void updatePredictCost(long transactionId, double predictCost, boolean isReliable);
}
