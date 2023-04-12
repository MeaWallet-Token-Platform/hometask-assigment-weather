package com.paymentology.weather.exception.handler;

import com.paymentology.weather.model.ErrorDto;

public class ExceptionHandlerMessageStore {

    public static final String CUSTOM_ERROR_VIEW = "errorView";
    public static final String ERROR = "error";

    private ExceptionHandlerMessageStore() {
    }

    public static String buildCustomBadRequestException(ErrorDto errorDto) {
        return "BadRequestException: " + errorDto;
    }

    public static String buildInternalServerError(ErrorDto errorDto) {
        return "InternalServerError: " + errorDto;
    }

}
