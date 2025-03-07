package com.paymentology.weather.domain.weather;

public record WeatherResponse(
        double temperature,
        String unit,
        double windSpeed,
        double windDirection,
        String timestamp
) {
    public static WeatherResponse from(Weather weather) {
        return new WeatherResponse(
                weather.temperature().value(),
                weather.temperature().unit().toString(),
                weather.wind().speed(),
                weather.wind().direction(),
                weather.timestamp().toString()
        );
    }
}
