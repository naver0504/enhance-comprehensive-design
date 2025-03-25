package com.example.command.batch.kakao_map;

import com.example.command.batch.BatchTag;
import com.example.command.api_client.kakao.KaKaoRestApiProperties;
import com.example.command.batch.kakao_map.dto.Documents;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest
@BatchTag
public class KaKaoMapApiTest {

    @Autowired
    KaKaoRestApiProperties kaKaoMapApi;

    @Test
    public void test() {
        RestTemplate restTemplate = new RestTemplate();
        //set header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kaKaoMapApi.key());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl("https://dapi.kakao.com/v2/local/search/address.json")
                .queryParam("query", "서울 송파구 백제고분로18길")
                .build();
        ResponseEntity<Documents> exchange = restTemplate.exchange(
                uriComponents.toUriString(),
                HttpMethod.GET,
                entity,
                Documents.class
        );
        System.out.println(exchange.getBody());
    }
}
