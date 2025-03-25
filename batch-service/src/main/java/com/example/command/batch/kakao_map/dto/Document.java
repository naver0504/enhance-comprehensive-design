package com.example.command.batch.kakao_map.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Document(@JsonProperty("address_name") String addressName,
                       @JsonProperty("address_type") AddressType addressType,
                       String x,
                       String y) {

    private enum AddressType {
        ROAD_ADDR, REGION_ADDR, ROAD
    }
}
