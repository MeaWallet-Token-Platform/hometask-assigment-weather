package com.paymentology.weather.util;

import com.paymentology.weather.constant.TemperatureUnit;
import org.springframework.stereotype.Component;

import static com.paymentology.weather.constant.TemperatureUnit.CELSIUS;
import static com.paymentology.weather.constant.TemperatureUnit.FAHRENHEIT;
import static com.paymentology.weather.constant.TemperatureUnit.KELVIN;

@Component
public class TemperatureUnitUtil {

    public TemperatureUnit determineUnit(String unitString) {

        return switch (unitString) {
            case String unit && KELVIN.toString().equalsIgnoreCase(unit) -> KELVIN;
            case String unit && FAHRENHEIT.toString().equalsIgnoreCase(unit) -> FAHRENHEIT;
            default -> CELSIUS;
        };

    }

}
