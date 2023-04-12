package com.paymentology.weather.model;

public record ClientApiKeyDto(long id, String apiKey, boolean revoked) {
}
