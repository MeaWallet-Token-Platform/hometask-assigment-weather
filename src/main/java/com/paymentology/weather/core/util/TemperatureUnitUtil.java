package com.paymentology.weather.core.util;

import com.paymentology.weather.core.constant.TemperatureUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TemperatureUnitUtil {

    public static final String NO_VALUE_SUBSTITUTING = "No value found for provided TemperatureUnit: %s, substituting with CELSIUS";

    public TemperatureUnit determineUnit(String unitString) {

        return switch (unitString) {
            case String unit && TemperatureUnit.KELVIN.toString().equalsIgnoreCase(unit) -> TemperatureUnit.KELVIN;
            case String unit && TemperatureUnit.FAHRENHEIT.toString().equalsIgnoreCase(unit) -> TemperatureUnit.FAHRENHEIT;
            case String unit && TemperatureUnit.CELSIUS.toString().equalsIgnoreCase(unit) -> TemperatureUnit.CELSIUS;
            case null, default -> {
                log.info(NO_VALUE_SUBSTITUTING.formatted(unitString));
                yield TemperatureUnit.CELSIUS;
            }
        };

    }

}
