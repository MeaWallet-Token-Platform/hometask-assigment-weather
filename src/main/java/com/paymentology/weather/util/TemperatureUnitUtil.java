package com.paymentology.weather.util;

import com.paymentology.weather.constant.TemperatureUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.paymentology.weather.constant.TemperatureUnit.CELSIUS;
import static com.paymentology.weather.constant.TemperatureUnit.FAHRENHEIT;
import static com.paymentology.weather.constant.TemperatureUnit.KELVIN;

@Component
@Slf4j
public class TemperatureUnitUtil {

    public static final String NO_VALUE_SUBSTITUTING = "No value found for provided TemperatureUnit: %s, substituting with CELSIUS";

    public TemperatureUnit determineUnit(String unitString) {

        return switch (unitString) {
            case String unit && KELVIN.toString().equalsIgnoreCase(unit) -> KELVIN;
            case String unit && FAHRENHEIT.toString().equalsIgnoreCase(unit) -> FAHRENHEIT;
            case String unit && CELSIUS.toString().equalsIgnoreCase(unit) -> CELSIUS;
            case null, default -> {
                log.info(NO_VALUE_SUBSTITUTING.formatted(unitString));
                yield CELSIUS;
            }
        };

    }

}
