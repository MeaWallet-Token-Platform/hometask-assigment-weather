package com.paymentology.weather.util;

import com.paymentology.weather.core.util.TemperatureUnitUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.paymentology.weather.core.constant.TemperatureUnit.CELSIUS;
import static com.paymentology.weather.core.constant.TemperatureUnit.FAHRENHEIT;
import static com.paymentology.weather.core.constant.TemperatureUnit.KELVIN;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TemperatureUnitUtilTest {

    @InjectMocks
    TemperatureUnitUtil victim;

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"celsius", "dummyUnit"})
    void determineUnit_whenCelsiusOrNullOrDummy_thenReturnCELSIUS(String arg) {
        var result = victim.determineUnit(arg);

        assertEquals(CELSIUS, result);
    }

    @Test
    void determineUnit_whenFahrenheit_thenReturnFAHRENHEIT() {
        var result = victim.determineUnit("fahrenheit");

        assertEquals(FAHRENHEIT, result);
    }

    @Test
    void determineUnit_whenKelvin_thenReturnKELVIN() {
        var result = victim.determineUnit("kelvin");

        assertEquals(KELVIN, result);
    }

}
