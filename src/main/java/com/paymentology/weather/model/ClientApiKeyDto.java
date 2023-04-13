package com.paymentology.weather.model;

public record ClientApiKeyDto(

        Long id,

        String apiKey,

        Boolean revoked

) {
}
