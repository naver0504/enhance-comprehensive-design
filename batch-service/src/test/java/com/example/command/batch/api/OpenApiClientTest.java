package com.example.command.batch.api;

import com.example.command.api_client.open_api.OpenApiClient;
import com.example.command.api_client.open_api.OpenApiRequest;
import com.example.command.api_client.open_api.OpenApiProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@SpringBootTest
@EnableConfigurationProperties(OpenApiProperties.class)
@ActiveProfiles("local")
class OpenApiClientTest  {

    @Autowired
    private OpenApiProperties openAPiProperties;

    @Test
    void callApiTest() {
        RestTemplate restTemplate = new RestTemplate();
        OpenApiClient openApiClient = new OpenApiClient(openAPiProperties, restTemplate, 1000);
        Assertions.assertDoesNotThrow(() -> openApiClient.callApi(new OpenApiRequest("11680", 1, LocalDate.now().minusMonths(1))));
    }
}