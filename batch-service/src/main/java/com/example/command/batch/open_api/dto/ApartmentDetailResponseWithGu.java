package com.example.command.batch.open_api.dto;

import com.example.command.domain.dong.Gu;

import java.util.List;

public record ApartmentDetailResponseWithGu(ApartmentDetailResponse apartmentDetailResponse, Gu gu) {

    public List<ApartmentDetail> toApartmentDetails() {
        return apartmentDetailResponse.toApartmentDetails();
    }
}
