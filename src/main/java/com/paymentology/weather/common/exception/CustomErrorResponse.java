package com.paymentology.weather.common.exception;

import java.time.Instant;

public record CustomErrorResponse(
        int status,
        String message,
        Instant timestamp
) {}
