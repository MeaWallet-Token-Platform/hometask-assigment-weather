package com.paymentology.weather.service.impl;

import com.paymentology.weather.infrastructure.service.RestTemplateService;
import com.paymentology.weather.core.model.GeoLocationDto;
import com.paymentology.weather.infrastructure.api.openmeteo.OpenMeteoResponseDto;
import com.paymentology.weather.core.model.WeatherDto;
import com.paymentology.weather.infrastructure.api.openmeteo.OpenMeteoProperties;
import com.paymentology.weather.infrastructure.api.openmeteo.OpenMeteoService;
import com.paymentology.weather.infrastructure.api.openmeteo.WeatherUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static com.paymentology.weather.core.constant.TemperatureUnit.CELSIUS;
import static com.paymentology.weather.test.uti.TestUtil.TEST_TEMPERATURE_UNIT;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationDto;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationEntity;
import static com.paymentology.weather.test.uti.TestUtil.newOpenMeteoResponseDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class OpenMeteoServiceTest {

    @Mock
    OpenMeteoProperties properties;
    @Mock
    RestTemplateService<OpenMeteoResponseDto> restTemplateService;
    @Mock
    WeatherUtil weatherUtil;

    @InjectMocks
    OpenMeteoService victim;


    private String url;
    private URI uri;
    private String expandedUrl;
    private GeoLocationDto geoLocationDto;
    private WeatherDto weatherDto;
    private OpenMeteoResponseDto openMeteoResponseDto;


    @BeforeEach
    void setUp() throws URISyntaxException {
        geoLocationDto = newGeoLocationDto(newGeoLocationEntity());
        url = "testUrl/{1}/{2}/{3}";
        openMeteoResponseDto = newOpenMeteoResponseDto();
        weatherDto = newWeatherDto(newWeatherEntity());

        expandedUrl = "testUrl/" +
                geoLocationDto.latitude() + "/" +
                geoLocationDto.longitude() + "/" +
                TEST_TEMPERATURE_UNIT.toLowerCase();

        uri = new URI(expandedUrl);
    }

    @AfterEach
    void windDown() {
        verifyNoMoreInteractions(properties, restTemplateService, weatherUtil);
    }

    @Test
    void findByLocationAndUnit_whenApiReturnsNull_thenReturnOptionalEmpty() {
        var expected = Optional.empty();
        given(weatherUtil.getApiUnit(CELSIUS)).willReturn(CELSIUS);
        given(properties.getCurrentWeatherUrl()).willReturn(url);
        given(weatherUtil.getCurrentWeatherUri(geoLocationDto, CELSIUS, url)).willReturn(uri);
        given(restTemplateService.getForObject(uri, OpenMeteoResponseDto.class)).willReturn(Optional.empty());

        var result = victim.findByLocationAndUnit(geoLocationDto, CELSIUS);

        assertEquals(expected, result);
    }

/*    @Test
    void findByLocationAndUnit_whenTemperatureUnitKelvin_thenChangeToCelsiusForApi() {
        var expected = Optional.empty();
        given(properties.getCurrentWeatherUrl()).willReturn(url);
        given(restTemplateService.getForObject(uri, OpenMeteoResponseDto.class)).willReturn(null);

        var result = victim.findByLocationAndUnit(geoLocationDto, KELVIN);

        assertEquals(expected, result);
    }*/

/*    @Test
    void findByLocationAndUnit_whenRestTemplateCatchesException_thenReturnOptionalEmpty() {
        var expected = Optional.empty();
        given(properties.getCurrentWeatherUrl()).willReturn(url);
        given(restTemplateService.getForObject(uri, OpenMeteoResponseDto.class)).willThrow(new RestClientException("exceptionMessage"));

        var result = victim.findByLocationAndUnit(geoLocationDto, CELSIUS);

        assertEquals(expected, result);
    }*/

    @Test
    void findByLocationAndUnit_whenApiProvidesResponse_thenReturn() {
        var expectedOptional = Optional.of(weatherDto);
        given(weatherUtil.getApiUnit(CELSIUS)).willReturn(CELSIUS);
        given(properties.getCurrentWeatherUrl()).willReturn(url);
        given(weatherUtil.getCurrentWeatherUri(geoLocationDto, CELSIUS, url)).willReturn(uri);
        given(restTemplateService.getForObject(uri, OpenMeteoResponseDto.class)).willReturn(Optional.of(openMeteoResponseDto));
        given(weatherUtil.applyKelvinLogic(CELSIUS, openMeteoResponseDto)).willReturn(openMeteoResponseDto);
        given(weatherUtil.createWeatherDto(openMeteoResponseDto, geoLocationDto, CELSIUS)).willReturn(expectedOptional.get());

        var resultOptional = victim.findByLocationAndUnit(geoLocationDto, CELSIUS);

        assertTrue(resultOptional.isPresent());
        assertEquals(expectedOptional, resultOptional);
        assertEquals(expectedOptional.get(), resultOptional.get());
    }


}
