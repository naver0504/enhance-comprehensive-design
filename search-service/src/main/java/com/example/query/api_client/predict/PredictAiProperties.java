package com.example.query.api_client.predict;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "predict.ai")
public record PredictAiProperties(String baseUrl,
                                  int port,
                                  String predictGraphPath) {
}
