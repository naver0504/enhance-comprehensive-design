package com.example.command.api_client.kakao;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao.rest-api")
public record KaKaoRestApiProperties(String key, String url) {
}
