package com.example.command.batch.open_api.dto;

import com.example.command.domain.dong.Gu;

import java.time.LocalDate;

public record AlreadyExistTransactioRecord(LocalDate dealDate, String jibun, String apartmentName,
                                           int floor, int buildYear, double areaForExclusiveUse, int dealAmount) {

    public AlreadyExistTransactioRecord(AlreadyExistTransactionRecordWithGu record) {
        this(record.dealDate(), record.jibun(), record.apartmentName(), record.floor(), record.buildYear(),
                record.areaForExclusiveUse(), record.dealAmount());
    }

    public boolean equals(ApartmentDetail apartmentDetail) {
        //jibun can be null
        return this.dealDate().equals(apartmentDetail.getDealDate()) &&
                this.apartmentName().equals(apartmentDetail.apartmentName()) &&
                this.floor() == apartmentDetail.floor() &&
                this.buildYear() == apartmentDetail.buildYear() &&
                this.areaForExclusiveUse() == apartmentDetail.areaForExclusiveUse() &&
                this.dealAmount() == apartmentDetail.getDealAmount() &&
                (this.jibun() == null || this.jibun().equals(apartmentDetail.jibun()));
    }

    public record AlreadyExistTransactionRecordWithGu(Gu gu, LocalDate dealDate, String jibun, String apartmentName,
                                                      int floor, int buildYear, double areaForExclusiveUse, int dealAmount) {
    }
}
