package com.example.command.batch.open_api.dto;

import com.example.command.domain.apartment.ApartmentTransaction;
import com.example.command.domain.apartment.DealingGbn;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@JacksonXmlRootElement(localName = "item")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApartmentDetail(@JacksonXmlProperty
                              @JsonDeserialize(using = DealAmountDeserializer.class)
                              int dealAmount,
                              @JacksonXmlProperty DealingGbn dealingGbn,
                              @JacksonXmlProperty int buildYear,
                              @JacksonXmlProperty int dealYear,
                              @JacksonXmlProperty int dealMonth,
                              @JacksonXmlProperty int dealDay,
                              @JacksonXmlProperty(localName = "umdNm")
                              @JsonDeserialize(using = StripStringDeserializer.class)
                              String dongName,
                              @JacksonXmlProperty(localName = "aptNm")
                              @JsonDeserialize(using = StripStringDeserializer.class)
                              String apartmentName,
                              @JacksonXmlProperty(localName = "excluUseAr") double areaForExclusiveUse,
                              @JacksonXmlProperty
                              @JsonDeserialize(using = StripStringDeserializer.class)
                              String jibun,
                              @JacksonXmlProperty int floor) {


    public ApartmentDetail(ApartmentTransaction apartmentTransaction) {
        this(apartmentTransaction.getDealAmount(),
                apartmentTransaction.getDealingGbn(),
                apartmentTransaction.getBuildYear(),
                apartmentTransaction.getDealYear(),
                apartmentTransaction.getDealMonth(),
                apartmentTransaction.getDealDay(),
                apartmentTransaction.getDongName(),
                apartmentTransaction.getApartmentName(),
                apartmentTransaction.getAreaForExclusiveUse(),
                apartmentTransaction.getJibun(),
                apartmentTransaction.getFloor());
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String FORMAT = "%d-%02d-%02d";


    private String createDealDate() {
        return String.format(FORMAT, dealYear, dealMonth, dealDay);
    }

    public LocalDate getDealDate() {
        return LocalDate.parse(createDealDate(), DATE_FORMATTER);
    }

    public static class DealAmountDeserializer extends JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws java.io.IOException {
            return Integer.parseInt(p.getText().replaceAll(",", ""));
        }
    }

    public static class StripStringDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws java.io.IOException {
            String text = p.getText();
            if (text == null) return null;
            return text.strip();
        }
    }
}