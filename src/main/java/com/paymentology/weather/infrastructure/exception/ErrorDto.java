package com.paymentology.weather.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorDto(

        Integer code,

        String error,

        String message,

        LocalDateTime timeStamp,

        String uri,

        String method,

        String cause

) {
    public ErrorDto(HttpStatus status, RuntimeException exception, HttpServletRequest request) {
        this(
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI(),
                request.getMethod(),
                exception.getClass().getSimpleName()
        );
    }

}
