package com.example.command.api_client.kakao;

import com.example.command.api_client.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

@RequiredArgsConstructor
public abstract class KaKaoApiClient<T, R> implements ApiClient<T, R> {

    private static final String HEADER_PREFIX = "KakaoAK ";
    private static final String QUERY_PREFIX = "서울 ";
    private final KaKaoRestApiProperties kaKaoRestApiProperties;

    @Override
    public String createUrl(T t) {
        return kaKaoRestApiProperties.url() + "?query="
                + QUERY_PREFIX + " " + getLocation(t);
    }
    
    protected abstract String getLocation(T t);

    public HttpEntity<String> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", HEADER_PREFIX + kaKaoRestApiProperties.key());
        return new HttpEntity<>(headers);
    }
}
