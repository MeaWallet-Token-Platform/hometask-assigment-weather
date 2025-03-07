package com.paymentology.weather.infra.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenMeteoResponse(
        @JsonProperty("current_weather")
        CurrentWeather currentWeather
) {
    public record CurrentWeather(
            double temperature,
            double windspeed,
            double winddirection
    ) {}
}
