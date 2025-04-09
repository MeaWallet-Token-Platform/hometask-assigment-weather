package com.paymentology.weather.infra.http.response;

import com.paymentology.weather.domain.weather.model;

import java.time.Instant;

public record WeatherJsonResponse(
        model.Temperature temperature,
        model.Wind wind,
        Instant timestamp) {

    public static WeatherJsonResponse fromWeather(model.Weather weather, Instant timestamp) {
        return new WeatherJsonResponse(weather.temperature(), weather.wind(), timestamp);
    }
}
