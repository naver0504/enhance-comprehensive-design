package com.example.command.config.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomHttpDetail {
    BAD_REQUEST(400),
    INTERNAL_SERVER_ERROR(500);

    private final int statusCode;
}
