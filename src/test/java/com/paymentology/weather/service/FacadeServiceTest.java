package com.paymentology.weather.service;

import com.paymentology.weather.constant.TemperatureUnit;
import com.paymentology.weather.exception.BadRequestException;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.util.GeoLocationUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.paymentology.weather.service.FacadeService.STANDARD_HOST;
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
    GeoLocationApiService geoLocationApiService;
    @Mock
    WeatherApiService weatherApiService;
    @Mock
    WeatherEntityService weatherEntityService;
    @Mock
    GeoLocationUtil geoLocationUtil;
    @InjectMocks
    FacadeService victim;
    private String host;
    private TemperatureUnit unit;
    private GeoLocationDto geoLocationDto;
    private GeoLocationDto standardGeoLocation;
    private WeatherDto weatherDto;

    @BeforeEach
    void setUp() {
        host = "testHost";
        unit = TemperatureUnit.CELSIUS;
        geoLocationDto = newGeoLocationDto(newGeoLocationEntity());
        standardGeoLocation = new GeoLocationDto(host, 25.5, 25.5);
        weatherDto = newWeatherDto(newWeatherEntity());
    }

    @AfterEach
    void windDown() {
        verifyNoMoreInteractions(geoLocationApiService, geoLocationEntityService,
                geoLocationUtil, weatherApiService, weatherEntityService);
    }


    @Test
    void findByUnitAndHost_whenRequest_thenFindAndReturn() {
        var expected = weatherDto;
        given(geoLocationApiService.findByHost(host)).willReturn(Optional.of(geoLocationDto));
        willDoNothing().given(geoLocationEntityService).saveOrUpdateAsync(geoLocationDto);
        given(weatherApiService.findByLocationAndUnit(geoLocationDto, unit)).willReturn(Optional.of(weatherDto));
        willDoNothing().given(weatherEntityService).saveUpdateAsync(weatherDto);

        var result = victim.findByUnitAndHost(unit, host);

        assertEquals(expected, result);
    }

    @Test
    void findByUnitAndHost_whenHostIsNul_andEmptyResponseFromGeoApi_thenFindInDb_andReturn() {
        var expected = weatherDto;
        given(geoLocationApiService.findByHost(STANDARD_HOST)).willReturn(Optional.empty());
        given(geoLocationEntityService.findByHost(STANDARD_HOST)).willReturn(Optional.of(geoLocationDto));
        given(weatherApiService.findByLocationAndUnit(geoLocationDto, unit)).willReturn(Optional.of(weatherDto));
        willDoNothing().given(weatherEntityService).saveUpdateAsync(weatherDto);

        var result = victim.findByUnitAndHost(unit, null);

        assertEquals(expected, result);
    }

    @Test
    void findByUnitAndHost_whenEmptyResponseFromGeoApi_andNothingInDb_thenUseStandardLocation_andReturn() {
        var expected = weatherDto;
        given(geoLocationApiService.findByHost(host)).willReturn(Optional.empty());
        given(geoLocationEntityService.findByHost(host)).willReturn(Optional.empty());
        given(geoLocationUtil.getStandardLocation(host)).willReturn(standardGeoLocation);
        given(weatherApiService.findByLocationAndUnit(standardGeoLocation, unit)).willReturn(Optional.of(weatherDto));
        willDoNothing().given(weatherEntityService).saveUpdateAsync(weatherDto);

        var result = victim.findByUnitAndHost(unit, host);

        assertEquals(expected, result);
    }

    @Test
    void findByUnitAndHost_whenEmptyResponseFromWeatherApi_thenFindInDb_andReturn() {
        var expected = weatherDto;
        given(geoLocationApiService.findByHost(host)).willReturn(Optional.of(geoLocationDto));
        willDoNothing().given(geoLocationEntityService).saveOrUpdateAsync(geoLocationDto);
        given(weatherApiService.findByLocationAndUnit(geoLocationDto, unit)).willReturn(Optional.empty());
        given(weatherEntityService.findByLocationAndUnit(geoLocationDto, unit)).willReturn(Optional.of(weatherDto));

        var result = victim.findByUnitAndHost(unit, host);

        assertEquals(expected, result);
    }

    @Test
    void findByUnitAndHost_whenEmptyResponseFromWeatherApi_andNothingInDb_thenThrowBadRequestException() {
        given(geoLocationApiService.findByHost(host)).willReturn(Optional.of(geoLocationDto));
        willDoNothing().given(geoLocationEntityService).saveOrUpdateAsync(geoLocationDto);
        given(weatherApiService.findByLocationAndUnit(geoLocationDto, unit)).willReturn(Optional.empty());
        given(weatherEntityService.findByLocationAndUnit(geoLocationDto, unit)).willReturn(Optional.empty());

        assertThatThrownBy(() -> victim.findByUnitAndHost(unit, host))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(host);
    }


}