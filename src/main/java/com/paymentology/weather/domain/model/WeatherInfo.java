package com.paymentology.weather.domain.model;

import java.time.Instant;

public final class WeatherInfo {
    private WeatherInfo() {
    }

    public record Temperature(double value, TemperatureUnit unit) {
    }

    public enum TemperatureUnit {
        Celsius,
        Fahrenheit,
        Kelvin
    }

    public record Wind(double speed, double direction) {
    }

    public record Weather(Temperature temperature, Wind wind, Instant timestamp) {
    }

}