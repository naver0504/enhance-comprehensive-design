package com.example.query.config;


import com.example.query.api_client.predict.PredictAiProperties;
import com.example.query.api_client.predict.PredictApiClientForGraph;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(PredictAiProperties.class)
public class ApiClientConfiguration {

    private final PredictAiProperties predictAiProperties;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PredictApiClientForGraph predictAiApiClient(RestTemplate restTemplate) {
        return new PredictApiClientForGraph(restTemplate, predictAiProperties);
    }
}
