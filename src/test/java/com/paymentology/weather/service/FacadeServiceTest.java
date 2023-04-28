package com.paymentology.weather.service;

import com.paymentology.weather.core.service.FacadeService;
import com.paymentology.weather.core.service.GeoLocationApiService;
import com.paymentology.weather.core.service.GeoLocationEntityService;
import com.paymentology.weather.core.service.WeatherApiService;
import com.paymentology.weather.core.service.WeatherEntityService;
import com.paymentology.weather.infrastructure.exception.BadRequestException;
import com.paymentology.weather.core.model.GeoLocationDto;
import com.paymentology.weather.core.model.WeatherDto;
import com.paymentology.weather.core.util.GeoLocationUtil;
import com.paymentology.weather.core.util.TemperatureUnitUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.paymentology.weather.core.constant.TemperatureUnit.CELSIUS;
import static com.paymentology.weather.core.service.FacadeService.STANDARD_HOST;
import static com.paymentology.weather.test.uti.TestUtil.TEST_HOST;
import static com.paymentology.weather.test.uti.TestUtil.TEST_TEMPERATURE_UNIT;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationDto;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationEntity;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherEntity;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class FacadeServiceTest {

    @Mock
    GeoLocationEntityService geoLocationEntityService;
    @Mock
    TemperatureUnitUtil temperatureUnitUtil;
    @Mock
    GeoLocationApiService geoLocationApiService;
    @Mock
    WeatherApiService weatherApiService;
    @Mock
    WeatherEntityService weatherEntityService;
    @Mock
    GeoLocationUtil geoLocationUtil;
    @InjectMocks
    FacadeService victim;


    private GeoLocationDto geoLocationDto;
    private GeoLocationDto standardGeoLocation;
    private WeatherDto weatherDto;


    @BeforeEach
    void setUp() {
        geoLocationDto = newGeoLocationDto(newGeoLocationEntity());
        standardGeoLocation = new GeoLocationDto(TEST_HOST, 25.5, 25.5);
        weatherDto = newWeatherDto(newWeatherEntity());
    }

    @AfterEach
    void windDown() {
        verifyNoMoreInteractions(geoLocationApiService, geoLocationEntityService,
                geoLocationUtil, weatherApiService, weatherEntityService, temperatureUnitUtil);
    }


    @Test
    void findByUnitAndHost_whenRequest_thenFindAndReturn() {
        var expected = weatherDto;
        given(temperatureUnitUtil.determineUnit(TEST_TEMPERATURE_UNIT)).willReturn(CELSIUS);
        given(geoLocationApiService.findByHost(TEST_HOST)).willReturn(Optional.of(geoLocationDto));
        willDoNothing().given(geoLocationEntityService).saveOrUpdateAsync(geoLocationDto);
        given(weatherApiService.findByLocationAndUnit(geoLocationDto, CELSIUS)).willReturn(Optional.of(weatherDto));
        willDoNothing().given(weatherEntityService).saveUpdateAsync(weatherDto);

        var result = victim.findByUnitAndHost(TEST_TEMPERATURE_UNIT, TEST_HOST);

        assertEquals(expected, result);
    }

    @Test
    void findByUnitAndHost_whenHostIsNul_andEmptyResponseFromGeoApi_thenFindInDb_andReturn() {
        var expected = weatherDto;
        given(temperatureUnitUtil.determineUnit(TEST_TEMPERATURE_UNIT)).willReturn(CELSIUS);
        given(geoLocationApiService.findByHost(STANDARD_HOST)).willReturn(Optional.empty());
        given(geoLocationEntityService.findByHost(STANDARD_HOST)).willReturn(Optional.of(geoLocationDto));
        given(weatherApiService.findByLocationAndUnit(geoLocationDto, CELSIUS)).willReturn(Optional.of(weatherDto));
        willDoNothing().given(weatherEntityService).saveUpdateAsync(weatherDto);

        var result = victim.findByUnitAndHost(TEST_TEMPERATURE_UNIT, null);

        assertEquals(expected, result);
    }

    @Test
    void findByUnitAndHost_whenEmptyResponseFromGeoApi_andNothingInDb_thenUseStandardLocation_andReturn() {
        var expected = weatherDto;
        given(temperatureUnitUtil.determineUnit(TEST_TEMPERATURE_UNIT)).willReturn(CELSIUS);
        given(geoLocationApiService.findByHost(TEST_HOST)).willReturn(Optional.empty());
        given(geoLocationEntityService.findByHost(TEST_HOST)).willReturn(Optional.empty());
        given(geoLocationUtil.getStandardLocation(TEST_HOST)).willReturn(standardGeoLocation);
        given(weatherApiService.findByLocationAndUnit(standardGeoLocation, CELSIUS)).willReturn(Optional.of(weatherDto));
        willDoNothing().given(weatherEntityService).saveUpdateAsync(weatherDto);

        var result = victim.findByUnitAndHost(TEST_TEMPERATURE_UNIT, TEST_HOST);

        assertEquals(expected, result);
    }

    @Test
    void findByUnitAndHost_whenEmptyResponseFromWeatherApi_thenFindInDb_andReturn() {
        var expected = weatherDto;
        given(temperatureUnitUtil.determineUnit(TEST_TEMPERATURE_UNIT)).willReturn(CELSIUS);
        given(geoLocationApiService.findByHost(TEST_HOST)).willReturn(Optional.of(geoLocationDto));
        willDoNothing().given(geoLocationEntityService).saveOrUpdateAsync(geoLocationDto);
        given(weatherApiService.findByLocationAndUnit(geoLocationDto, CELSIUS)).willReturn(Optional.empty());
        given(weatherEntityService.findByLocationAndUnit(geoLocationDto, CELSIUS)).willReturn(Optional.of(weatherDto));

        var result = victim.findByUnitAndHost(TEST_TEMPERATURE_UNIT, TEST_HOST);

        assertEquals(expected, result);
    }

    @Test
    void findByUnitAndHost_whenEmptyResponseFromWeatherApi_andNothingInDb_thenThrowBadRequestException() {
        given(temperatureUnitUtil.determineUnit(TEST_TEMPERATURE_UNIT)).willReturn(CELSIUS);
        given(geoLocationApiService.findByHost(TEST_HOST)).willReturn(Optional.of(geoLocationDto));
        willDoNothing().given(geoLocationEntityService).saveOrUpdateAsync(geoLocationDto);
        given(weatherApiService.findByLocationAndUnit(geoLocationDto, CELSIUS)).willReturn(Optional.empty());
        given(weatherEntityService.findByLocationAndUnit(geoLocationDto, CELSIUS)).willReturn(Optional.empty());

        assertThatThrownBy(() -> victim.findByUnitAndHost(TEST_TEMPERATURE_UNIT, TEST_HOST))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(TEST_HOST);
    }


}
