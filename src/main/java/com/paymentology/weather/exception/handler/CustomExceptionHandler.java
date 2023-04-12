package com.paymentology.weather.exception.handler;

import com.paymentology.weather.exception.BadRequestException;
import com.paymentology.weather.model.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

import static com.paymentology.weather.exception.handler.ExceptionHandlerMessageStore.CUSTOM_ERROR_VIEW;
import static com.paymentology.weather.exception.handler.ExceptionHandlerMessageStore.ERROR;
import static com.paymentology.weather.exception.handler.ExceptionHandlerMessageStore.buildCustomBadRequestException;
import static com.paymentology.weather.exception.handler.ExceptionHandlerMessageStore.buildInternalServerError;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    public static final String API_CALL = "ApiCall: ";

    @ExceptionHandler(BindException.class)
    public String handleBindException(Model model, BindException ex, HttpServletRequest request) {
        String exceptionMessage = ex.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorDto errorDto = new ErrorDto(BAD_REQUEST, exceptionMessage, request);
        log.warn(buildCustomBadRequestException(errorDto));
        model.addAttribute(ERROR, errorDto);
        return CUSTOM_ERROR_VIEW;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingServletRequestParameterException(Model model, MissingServletRequestParameterException ex, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(BAD_REQUEST, ex.getMessage(), request);
        log.warn(buildCustomBadRequestException(errorDto));
        model.addAttribute(ERROR, errorDto);
        return CUSTOM_ERROR_VIEW;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleBindException(Model model, ConstraintViolationException ex, HttpServletRequest request) {
        String exceptionMessage = ex.getConstraintViolations().stream()
                .map(error -> error.getPropertyPath() + ": " + error.getMessage())
                .collect(Collectors.joining(", "));

        ErrorDto errorDto = new ErrorDto(BAD_REQUEST, exceptionMessage, request);
        log.warn(buildCustomBadRequestException(errorDto));
        model.addAttribute(ERROR, errorDto);
        return CUSTOM_ERROR_VIEW;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(Model model, MethodArgumentNotValidException ex, HttpServletRequest request) {
        String exceptionMessage = ex.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorDto errorDto = new ErrorDto(BAD_REQUEST, exceptionMessage, request);
        log.warn(buildCustomBadRequestException(errorDto));
        model.addAttribute(ERROR, errorDto);
        return CUSTOM_ERROR_VIEW;
    }

    @ExceptionHandler(BadRequestException.class)
    public String handleCustomBadRequestException(Model model, BadRequestException ex, HttpServletRequest request) {
        var errorDto = new ErrorDto(BAD_REQUEST, ex, request);
        log.warn(buildCustomBadRequestException(errorDto));
        model.addAttribute(ERROR, errorDto);
        return CUSTOM_ERROR_VIEW;
    }
/*
    @ExceptionHandler(ApiException.class)
    public String handleApiException(Model model, ApiException ex) {
        log.warn(buildCustomBadRequestException(ex.getErrorDto()));
        model.addAttribute(ERROR, ex.getErrorDto());
        return CUSTOM_ERROR_VIEW;
    }*/

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolationException(Model model, DataIntegrityViolationException ex, HttpServletRequest request) {
        ex.printStackTrace();
        var errorDto = new ErrorDto(INTERNAL_SERVER_ERROR, ex.getCause().getCause().getMessage(), request);
        log.warn(buildInternalServerError(errorDto));
        model.addAttribute(ERROR, errorDto);
        return CUSTOM_ERROR_VIEW;
    }

/*
    @ExceptionHandler(HttpClientErrorException.class)
    public String handleHttpClientErrorException(Model model, HttpClientErrorException ex, HttpServletRequest request) {
        var errorDto = new ErrorDto(HttpStatus.resolve(ex.getStatusCode().value()), ex.getMessage(), request);
        log.warn(buildInternalServerError(errorDto));
        model.addAttribute(ERROR, errorDto);
        return CUSTOM_ERROR_VIEW;
    }


    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(Model model, RuntimeException ex, HttpServletRequest request) {
        ex.printStackTrace();
        var errorDto = new ErrorDto(INTERNAL_SERVER_ERROR, ex.getMessage(), ex.toString(), request);
        log.warn(buildInternalServerError(errorDto));
        model.addAttribute(ERROR, errorDto);
        return CUSTOM_ERROR_VIEW;
    }*/

}
