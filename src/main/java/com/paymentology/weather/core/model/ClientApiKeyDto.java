package com.paymentology.weather.core.model;

public record ClientApiKeyDto(

        Long id,

        String apiKey,

        Boolean revoked

) {
}
