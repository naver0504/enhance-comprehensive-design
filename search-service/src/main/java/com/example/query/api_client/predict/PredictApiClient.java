package com.example.query.api_client.predict;


import com.example.query.api_client.ApiClient;
import com.example.query.api_client.predict.dto.ApartmentQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@RequiredArgsConstructor
public abstract class PredictApiClient<T  extends ApartmentQuery, R> implements ApiClient<T  , R> {

    protected final PredictAiProperties predictAiProperties;

    @Override
    public String createUrl(T t) {
        return createURI(t);
    }

    private String createURI(T t) {
        return predictAiProperties.baseUrl() + ":" + predictAiProperties.port()
                + "/" + createPath()
                + "?gu=" + t.getGu()
                + "&dong=" + t.getDongName()
                + "&exclusiveArea=" + t.getAreaForExclusiveUse()
                + "&floor=" + t.getFloor()
                + "&buildYear=" + t.getBuildYear();
    }

    protected HttpEntity<?> createHttpEntities() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    protected abstract String createPath();
}
