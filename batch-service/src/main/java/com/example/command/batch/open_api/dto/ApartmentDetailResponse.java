package com.example.command.batch.open_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApartmentDetailResponse(@JacksonXmlProperty(localName = "header") ApartmentHeader header,
                                      @JacksonXmlProperty(localName = "body") ApartmentDetailBody body) {

    public boolean isLimitExceeded() {
        return header().resultCode() == 99;
    }

    public boolean isEndOfData() {
        return body().totalCount() == 0;
    }

    public boolean isEndOfGu() {
        return body().items().isEmpty();
    }

    public int getTotalCount() {
        return body().totalCount();
    }

    public List<ApartmentDetail> toApartmentDetails() {
        return body().items();
    }
}


