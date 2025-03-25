package com.example.command.api_client.open_api;


import com.example.command.api_client.ApiClient;
import com.example.command.batch.open_api.dto.ApartmentDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.net.URI;


@RequiredArgsConstructor
@Slf4j
public class OpenApiClient implements ApiClient<OpenApiRequest, ApartmentDetailResponse> {

    private final OpenApiProperties openAPiProperties;
    private final RestTemplate restTemplate;
    private final int NUM_OF_ROWS;

    @Override
    public String createUrl(OpenApiRequest openApiRequest) {
        return openAPiProperties.endPoint() + "?" + "serviceKey=" + openAPiProperties.serviceKey() +
                "&" + "numOfRows=" + NUM_OF_ROWS +
                "&" + "LAWD_CD=" + openApiRequest.regionalCode() +
                "&" + "pageNo=" + openApiRequest.pageNo() +
                "&" + "DEAL_YMD=" + openApiRequest.contractDate();    }

    @Override
    public ApartmentDetailResponse callApi(OpenApiRequest openApiRequest) {
        //URI.create()를 사용하지 않고 String Type을 넣으면 serviceKey에 특수문자가 들어가면 RestTemplate이 인코딩을 해버림
        return restTemplate.getForObject(URI.create(createUrl(openApiRequest)), ApartmentDetailResponse.class);
    }
}
