package com.paymentology.weather.domain.weather;

public class TemperatureConverter {
    public static Double convertTemperatureUnit(model.Weather weather, model.TemperatureUnit targetUnit) {
        return switch (weather.temperature().unit()) {
            case Celsius -> switch (targetUnit) {
                case Fahrenheit -> weather.temperature().value() * 9.0/5.0 + 32;
                case Kelvin -> weather.temperature().value() + 273.15;
                default -> weather.temperature().value();
            };
            case Fahrenheit -> switch (targetUnit) {
                case Celsius -> (weather.temperature().value() - 32) * 5.0/9.0;
                case Kelvin -> (weather.temperature().value() - 32) * 5.0/9.0 + 273.15;
                default -> weather.temperature().value();
            };
            case Kelvin -> switch (targetUnit) {
                case Celsius -> weather.temperature().value() - 273.15;
                case Fahrenheit -> (weather.temperature().value() - 273.15) * 9.0/5.0 + 32;
                default -> weather.temperature().value();
            };
        };
    }
}
