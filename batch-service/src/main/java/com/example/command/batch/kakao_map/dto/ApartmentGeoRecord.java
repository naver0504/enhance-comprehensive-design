package com.example.command.batch.kakao_map.dto;


public record ApartmentGeoRecord (Long id, String x, String y) {

    public boolean isNotEmpty() {
        return x != null && y != null;
    }

    public String toPoint() {
        return String.format("POINT(%s %s)", x, y);
    }
}
