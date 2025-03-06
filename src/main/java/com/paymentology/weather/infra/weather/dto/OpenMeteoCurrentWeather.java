package com.paymentology.weather.infra.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoCurrentWeather(
        double temperature,
        @JsonProperty("windspeed") double windSpeed,
        @JsonProperty("winddirection") double windDirection
) {
}
