package com.paymentology.weather.model;

import com.paymentology.weather.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ErrorDto {

    private int code;
    private String error;
    private String message;
    private LocalDateTime timeStamp;
    private String uri;
    private String method;
    private String cause;

    public ErrorDto(HttpStatus status, BadRequestException exception, HttpServletRequest request) {
        this.code = status.value();
        this.error = status.getReasonPhrase();
        this.message = exception.getMessage();
        this.cause = exception.getClass().getSimpleName();
        this.uri = request.getRequestURI();
        this.method = request.getMethod();
        this.timeStamp = LocalDateTime.now();
    }

    public ErrorDto(HttpStatus status, String message, HttpServletRequest request) {
        this.code = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.cause = null;
        this.uri = request.getRequestURI();
        this.method = request.getMethod();
        this.timeStamp = LocalDateTime.now();
    }

    public ErrorDto() {
    }
}
