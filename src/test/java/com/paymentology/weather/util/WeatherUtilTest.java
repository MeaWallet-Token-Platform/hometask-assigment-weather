package com.paymentology.weather.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.paymentology.weather.constant.TemperatureUnit.CELSIUS;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationDto;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationEntity;
import static com.paymentology.weather.test.uti.TestUtil.newOpenMeteoResponseDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherEntity;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class WeatherUtilTest {

    @InjectMocks
    WeatherUtil victim;

    @Test
    void createResponseDto_whenValidRequest_thenReturnResponseDto() {
        var expected = newWeatherDto(newWeatherEntity());

        var result = victim.createResponseDto(newOpenMeteoResponseDto(), newGeoLocationDto(newGeoLocationEntity()), CELSIUS);

        assertEquals(expected.host(), result.host());
        assertEquals(expected.temperature(), result.temperature());
        assertEquals(expected.wind(), result.wind());
        assertNotNull(result.timestamp());
    }

    @Test
    void createResponseDto_whenOpenMeteoResponseDtoIsNull_thenThrowNullPointerException() {
        var geoLocationDto = newGeoLocationDto(newGeoLocationEntity());

        assertThatThrownBy(() -> victim.createResponseDto(null, geoLocationDto, CELSIUS))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void createResponseDto_whenGeoLocationDtoIsNull_thenThrowNullPointerException() {
        var openMeteoResponseDto = newOpenMeteoResponseDto();

        assertThatThrownBy(() -> victim.createResponseDto(openMeteoResponseDto, null, CELSIUS))
                .isInstanceOf(NullPointerException.class);
    }

}
