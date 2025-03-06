package com.paymentology.weather.domain.weather;

import java.util.Arrays;

public enum TemperatureUnit {
    CELSIUS,
    FAHRENHEIT;

    public static TemperatureUnit getByQueryParam(String queryParam) {
        return Arrays.stream(values())
                .filter(u -> u.name().equalsIgnoreCase(queryParam))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Temporal unit not found for query param " + queryParam));

    }
}
