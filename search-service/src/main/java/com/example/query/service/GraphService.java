package com.example.query.service;

import com.example.query.adapter.ApartmentTransactionAdapter;
import com.example.query.adapter.document.ApartmentTransaction;
import com.example.query.api_client.predict.PredictApiClientForGraph;
import com.example.query.api_client.predict.dto.ApartmentGraphQuery;
import com.example.query.dto.response.GraphResponse;
import com.example.query.dto.response.PredictAiResponse;
import com.example.query.dto.response.RealTransactionGraphResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GraphService {

    private final ApartmentTransactionAdapter apartmentTransactionAdapter;
    private final PredictApiClientForGraph predictAiApiClient;

    public GraphResponse findApartmentTransactionsForGraph(long transactionId) {
        ApartmentTransaction apartmentTransaction = apartmentTransactionAdapter.findApartmentTransactionById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("잘못 된 거래 Id 입니다."));

        List<ApartmentTransaction> apartmentTransactionsForGraph = apartmentTransactionAdapter.findApartmentTransactionsForGraph(
                apartmentTransaction.getGu(),
                apartmentTransaction.getDong(),
                apartmentTransaction.getApartmentName(),
                apartmentTransaction.getAreaForExclusiveUse(),
                apartmentTransaction.getDealDate().minusMonths(12),
                apartmentTransaction.getDealDate()
        );

        RealTransactionGraphResponse realTransactionGraphResponse = new RealTransactionGraphResponse(apartmentTransactionsForGraph);
        PredictAiResponse predictAiResponse = predictAiApiClient.callApi(new ApartmentGraphQuery(apartmentTransaction));
        return new GraphResponse(realTransactionGraphResponse, predictAiResponse);
    }
}
