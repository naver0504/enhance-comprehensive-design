package com.example.command.api_client.predict;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "predict.ai")
public record PredictAiProperties(String baseUrl,
                                  int port,
                                  String predictCostPath, String predictGraphPath) {

}
