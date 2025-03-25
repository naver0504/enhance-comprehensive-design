package com.example.command.batch.kakao_map.dto;



public record LocationRecord(String x, String y) {

    public static final LocationRecord EMPTY = new LocationRecord(null, null);

    public ApartmentGeoRecord toApartmentGeoRecord(long id) {
        return new ApartmentGeoRecord(id, x, y);
    }

}
