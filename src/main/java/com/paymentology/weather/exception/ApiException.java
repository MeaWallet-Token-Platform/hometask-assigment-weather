package com.paymentology.weather.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }
}
