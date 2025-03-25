package com.example.command.batch.kakao_map.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Documents(List<Document> documents, Meta meta) {

    public LocationRecord toLocationRecord() {
        if(documents.isEmpty()) {
            return LocationRecord.EMPTY;
        }
        return new LocationRecord(documents.get(0).x(), documents.get(0).y());
    }

    public record Meta(@JsonProperty("is_end") boolean isEnd,
                       @JsonProperty("total_count") int totalCount) {
    }
}
