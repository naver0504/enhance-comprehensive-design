package com.example.query.api_client.predict;


import com.example.query.api_client.predict.dto.ApartmentQuery;
import com.example.query.dto.response.PredictAiResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class PredictApiClientForGraph extends PredictApiClient<ApartmentQuery, PredictAiResponse> {

    private final RestTemplate restTemplate;

    public PredictApiClientForGraph(RestTemplate restTemplate, PredictAiProperties predictAiProperties) {
        super(predictAiProperties);
        this.restTemplate = restTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Cacheable(value = "predictAi", key = "#apartmentQuery.getGu() + ':' + #apartmentQuery.getDongName() + ':' + #apartmentQuery.getAreaForExclusiveUse() + ':' + #apartmentQuery.getFloor() + ':' + #apartmentQuery.getBuildYear()")
    public PredictAiResponse callApi(ApartmentQuery apartmentQuery) {
        return new PredictAiResponse(restTemplate.exchange(
                createUrl(apartmentQuery),
                HttpMethod.GET,
                createHttpEntities(),
                Map.class
        ).getBody());
    }

    @Override
    protected String createPath() {
        return predictAiProperties.predictGraphPath();
    }
}
