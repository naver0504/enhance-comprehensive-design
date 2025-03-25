package com.example.command.config;

import com.example.command.api_client.predict.PredictAiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(PredictAiProperties.class)
public class ApiClientConfiguration implements WebMvcConfigurer {

    private final PredictAiProperties predictAiProperties;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "https://budda-dgu.netlify.app/")
                .allowedMethods(Stream.of(HttpMethod.values())
                        .map(HttpMethod::name)
                        .toArray(String[]::new));
    }
}
