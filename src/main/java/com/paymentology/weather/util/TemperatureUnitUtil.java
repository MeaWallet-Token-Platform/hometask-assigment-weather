package com.paymentology.weather.util;

import com.paymentology.weather.constant.TemperatureUnit;
import org.springframework.stereotype.Component;

import static com.paymentology.weather.constant.TemperatureUnit.CELSIUS;
import static com.paymentology.weather.constant.TemperatureUnit.FAHRENHEIT;
import static com.paymentology.weather.constant.TemperatureUnit.KELVIN;

@Component
public class TemperatureUnitUtil {

    public TemperatureUnit determineUnit(String unitString) {

        if (FAHRENHEIT.toString().equalsIgnoreCase(unitString)) {
            return FAHRENHEIT;
        } else if (KELVIN.toString().equalsIgnoreCase(unitString)) {
            return KELVIN;
        } else {
            return CELSIUS;
        }

    }

}
