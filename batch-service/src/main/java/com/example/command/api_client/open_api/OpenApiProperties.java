package com.example.command.api_client.open_api;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "open.api")
public record OpenApiProperties(String endPoint, String serviceKey) {}
