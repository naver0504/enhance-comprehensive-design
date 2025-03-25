package com.example.query.service;


import com.example.query.adapter.ApartmentTransactionAdapter;
import com.example.query.adapter.document.ApartmentTransaction;
import com.example.query.adapter.order.CustomPageable;
import com.example.query.dto.request.SearchApartNameRequest;
import com.example.query.dto.request.SearchAreaRequest;
import com.example.query.dto.request.SearchCondition;
import com.example.query.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentTransactionService {

    private final ApartmentTransactionAdapter apartmentTransactionAdapter;

    public Page<SearchResponseRecord> searchApartmentTransactions(Long cachedCount, SearchCondition searchCondition, CustomPageable customPageable) {
        if(searchCondition.isNotValid()) throw new IllegalArgumentException("검색 조건이 올바르지 않습니다.");
        return apartmentTransactionAdapter.searchApartmentTransactions(cachedCount, searchCondition, customPageable);
    }

    public List<SearchApartNameResponse> findApartmentNames(SearchApartNameRequest request) {
        if(request.isNotValid()) throw new IllegalArgumentException("검색 조건이 올바르지 않습니다.");
        return apartmentTransactionAdapter.findApartmentNames(request.getGu(), request.getDong());
    }

    public List<SearchAreaResponse> findAreaForExclusive(SearchAreaRequest request) {
        if(request.isNotValid()) throw new IllegalArgumentException("검색 조건이 올바르지 않습니다.");
        return apartmentTransactionAdapter.findAreaForExclusive(request.getGu(), request.getDong(), request.getApartmentName());
    }

    public TransactionDetailResponse findTransactionDetail(long id) {
        return apartmentTransactionAdapter.findApartmentTransactionById(id)
                .map(TransactionDetailResponse::new)
                .orElseThrow(() -> new IllegalArgumentException("잘못 된 거래 Id 입니다."));
    }
}
