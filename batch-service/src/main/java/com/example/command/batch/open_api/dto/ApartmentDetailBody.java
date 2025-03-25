package com.example.command.batch.open_api.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "body")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApartmentDetailBody(@JacksonXmlProperty(localName = "items") List<ApartmentDetail> items,
                                  @JacksonXmlProperty(localName = "numOfRows") int numOfRows,
                                  @JacksonXmlProperty(localName = "totalCount") int totalCount) { }
