package com.example.command.batch.excel_write.dto;

import com.example.command.domain.AddressUtils;
import com.example.command.domain.apartment.ApartmentTransaction;
import com.example.command.domain.dong.DongEntity;
import com.example.command.domain.interest.Interest;

public record ExcelQueryRecord(
        // QuerydslNoOffsetIdPagingItemReader 작동하기 위해서는 ID 필드가 필요함
        long id,
        ApartmentTransaction apartmentTransaction,
        DongEntity dongEntity,
        Interest interest
) {

    public ExcelOutputRecord toExcelOutputRecord() {
        return new ExcelOutputRecord(
                apartmentTransaction.getDealDate(),
                interest.getInterestRate(),
                dongEntity.getGu(),
                dongEntity.getDongName(),
                apartmentTransaction.getAreaForExclusiveUse(),
                apartmentTransaction.getFloor(),
                apartmentTransaction.getBuildYear(),
                apartmentTransaction.getDealAmount(),
                apartmentTransaction.getApartmentName(),
                AddressUtils.getJibunAddress(dongEntity.getGu(), dongEntity.getDongName(), apartmentTransaction.getJibun())
                        .orElse(null)
        );
    }
}
