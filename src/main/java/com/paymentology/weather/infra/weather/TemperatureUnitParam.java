package com.paymentology.weather.infra.weather;

import com.paymentology.weather.domain.weather.TemperatureUnit;

public enum TemperatureUnitParam {
    CELSIUS, FAHRENHEIT, KELVIN;

    public TemperatureUnit toTemperatureUnit() {
        return switch (this) {
            case CELSIUS -> TemperatureUnit.Celsius;
            case FAHRENHEIT -> TemperatureUnit.Fahrenheit;
            case KELVIN -> TemperatureUnit.Kelvin;
        };
    }
}
