package com.paymentology.weather.exception.handler;

import com.paymentology.weather.exception.BadRequestException;
import com.paymentology.weather.model.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDto> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        var errorDto = new ErrorDto(HttpStatus.BAD_REQUEST, ex, request);
        log.info(errorDto.toString());

        return ResponseEntity
                .badRequest()
                .body(errorDto);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        ex.printStackTrace();
        var errorDto = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
        log.warn(errorDto.toString());

        return ResponseEntity
                .internalServerError()
                .body(errorDto);
    }

}
