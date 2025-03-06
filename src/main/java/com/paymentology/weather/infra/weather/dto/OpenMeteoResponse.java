package com.paymentology.weather.infra.weather.dto;

import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoResponse(
        double latitude,
        double longitude,
        @JsonProperty("current_weather")
        OpenMeteoCurrentWeather currentWeather
) {
}
