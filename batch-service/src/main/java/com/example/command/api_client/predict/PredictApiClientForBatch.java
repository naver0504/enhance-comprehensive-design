package com.example.command.api_client.predict;

import com.example.command.api_client.predict.dto.PredictCostResponse;
import com.example.command.domain.apartment.ApartmentTransaction;
import com.example.command.domain.predict_cost.PredictCost;
import com.example.command.domain.predict_cost.PredictStatus;
import com.example.command.api_client.predict.dto.ApartmentBatchQuery;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public class PredictApiClientForBatch extends PredictApiClient<ApartmentBatchQuery, PredictCost> {

    private final RestTemplate restTemplate;

    public PredictApiClientForBatch(PredictAiProperties predictAiProperties, RestTemplate restTemplate) {
        super(predictAiProperties);
        this.restTemplate = restTemplate;
    }

    @Override
    protected String createPath() {
        return predictAiProperties.predictCostPath();
    }

    @Override
    public String createUrl(ApartmentBatchQuery apartmentQueryRecord) {
        return super.createUrl(apartmentQueryRecord)
                + "&interestRate=" + apartmentQueryRecord.getInterestRate()
                + "&dealDate=" + apartmentQueryRecord.getDealDate()
                + "&dealAmount=" + apartmentQueryRecord.getDealAmount();
    }

    @Override
    public PredictCost callApi(ApartmentBatchQuery apartmentQueryRecord) {
        PredictCostResponse response = restTemplate.exchange(
                createUrl(apartmentQueryRecord),
                HttpMethod.GET,
                createHttpEntities(),
                PredictCostResponse.class
        ).getBody();

        return PredictCost.builder()
                .predictedCost(Objects.requireNonNull(response).prediction())
                .isReliable(response.reliable())
                .predictStatus(PredictStatus.RECENT)
                .apartmentTransaction(ApartmentTransaction.builder().id(apartmentQueryRecord.getId()).build())
                .build();
    }

}
