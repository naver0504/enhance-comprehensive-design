package com.example.command.batch.open_api.dto;

import com.example.command.domain.AddressUtils;
import com.example.command.domain.apartment.DealingGbn;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@JacksonXmlRootElement(localName = "item")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApartmentDetail(@JacksonXmlProperty String dealAmount,
                              @JacksonXmlProperty DealingGbn dealingGbn,
                              @JacksonXmlProperty int buildYear,
                              @JacksonXmlProperty int dealYear,
                              @JacksonXmlProperty int dealMonth,
                              @JacksonXmlProperty int dealDay,
                              @JacksonXmlProperty(localName = "umdNm") String dongName,
                              @JacksonXmlProperty(localName = "aptNm") String apartmentName,
                              @JacksonXmlProperty(localName = "excluUseAr") double areaForExclusiveUse,
                              @JacksonXmlProperty String jibun,
                              @JacksonXmlProperty int floor) {


    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String FORMAT = "%d-%02d-%02d";


    private String createDealDate() {
        return String.format(FORMAT, dealYear, dealMonth, dealDay);
    }

    public LocalDate getDealDate() {
        return LocalDate.parse(createDealDate(), DATE_FORMATTER);
    }

    public int getDealAmount() {
        return Integer.parseInt(dealAmount.replaceAll(",", ""));
    }
}