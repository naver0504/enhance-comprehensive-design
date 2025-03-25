package com.example.command.api_client.open_api;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public record OpenApiRequest(String regionalCode, int pageNo, String contractDate) {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    public OpenApiRequest(String regionalCode, int pageNo, LocalDate contractDate) {
        this(regionalCode, pageNo, FORMATTER.format(contractDate));
    }
}
