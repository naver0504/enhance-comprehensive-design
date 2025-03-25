package com.example.command.batch.open_api;

import com.example.command.api_client.open_api.OpenApiClient;
import com.example.command.api_client.open_api.OpenApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiBaseBatchConfiguration {

    public static final int NUM_OF_ROWS = 1000;

    private final OpenApiProperties openApiProperties;
    private final RestTemplate restTemplate;

    @Bean
    public Integer numOfRows() {
        return NUM_OF_ROWS;
    }

    @Bean
    @StepScope
    public OpenApiClient openApiClient() {
        return new OpenApiClient(openApiProperties, restTemplate, numOfRows());
    }
}
