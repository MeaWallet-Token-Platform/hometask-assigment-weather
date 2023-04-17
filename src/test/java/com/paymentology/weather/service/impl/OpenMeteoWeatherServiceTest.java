package com.paymentology.weather.service.impl;

import com.paymentology.weather.constant.TemperatureUnit;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.OpenMeteoResponseDto;
import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.properties.OpenMeteoProperties;
import com.paymentology.weather.util.WeatherUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationDto;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationEntity;
import static com.paymentology.weather.test.uti.TestUtil.newOpenMeteoResponseDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class OpenMeteoWeatherServiceTest {

    private String url;
    private String expandedUrl;
    private TemperatureUnit unit;
    private GeoLocationDto geoLocationDto;
    private WeatherDto weatherDto;
    private OpenMeteoResponseDto openMeteoResponseDto;

    @Mock
    OpenMeteoProperties properties;
    @Mock
    RestTemplate restTemplate;
    @Mock
    WeatherUtil weatherUtil;

    @InjectMocks
    OpenMeteoWeatherService victim;

    @BeforeEach
    void setUp() {
        geoLocationDto = newGeoLocationDto(newGeoLocationEntity());
        url = "testUrl/{1}/{2}/{3}";
        unit = TemperatureUnit.CELSIUS;
        openMeteoResponseDto = newOpenMeteoResponseDto();
        weatherDto = newWeatherDto(newWeatherEntity());

        expandedUrl = "testUrl/" +
                geoLocationDto.latitude() + "/" +
                geoLocationDto.longitude() + "/" +
                unit.toString().toLowerCase();
    }

    @AfterEach
    void windDown() {
        verifyNoMoreInteractions(properties, restTemplate, weatherUtil);
    }

    @Test
    void findByLocationAndUnit_whenApiReturnsNull_thenReturnOptionalEmpty() throws URISyntaxException {
        var expected = Optional.empty();
        var uri = new URI(expandedUrl);
        given(properties.getCurrentWeatherUrl()).willReturn(url);
        given(restTemplate.getForObject(uri, OpenMeteoResponseDto.class)).willReturn(null);

        var result = victim.findByLocationAndUnit(geoLocationDto, unit);

        assertEquals(expected, result);
    }

    @Test
    void findByLocationAndUnit_whenRestTemplateCatchesException_thenReturnOptionalEmpty() throws URISyntaxException {
        var expected = Optional.empty();
        var uri = new URI(expandedUrl);
        given(properties.getCurrentWeatherUrl()).willReturn(url);
        given(restTemplate.getForObject(uri, OpenMeteoResponseDto.class)).willThrow(new RestClientException("exceptionMessage"));

        var result = victim.findByLocationAndUnit(geoLocationDto, unit);

        assertEquals(expected, result);
    }

    @Test
    void findByLocationAndUnit_whenApiProvidesResponse_thenReturn() throws URISyntaxException {
        var expected = Optional.of(weatherDto);
        var uri = new URI(expandedUrl);
        given(properties.getCurrentWeatherUrl()).willReturn(url);
        given(restTemplate.getForObject(uri, OpenMeteoResponseDto.class)).willReturn(openMeteoResponseDto);
        given(weatherUtil.createResponseDto(openMeteoResponseDto, geoLocationDto, unit)).willReturn(weatherDto);

        var result = victim.findByLocationAndUnit(geoLocationDto, unit);

        assertEquals(expected, result);
    }



}