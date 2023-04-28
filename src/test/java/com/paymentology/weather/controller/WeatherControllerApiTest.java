package com.paymentology.weather.controller;

import com.paymentology.weather.core.model.WeatherDto;
import com.paymentology.weather.core.service.FacadeService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static com.paymentology.weather.test.uti.TestUtil.TEST_HEADER;
import static com.paymentology.weather.test.uti.TestUtil.TEST_TEMPERATURE_UNIT;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class WeatherControllerApiTest {

    HttpServletRequest requestMock;
    WeatherDto weatherDto;

    @Mock
    FacadeService service;

    @InjectMocks
    WeatherControllerApi victim;

    @BeforeEach
    void setUp() {
        requestMock = mock(HttpServletRequest.class);
        weatherDto = newWeatherDto(newWeatherEntity());
    }

    @AfterEach
    void windDown() {
        verifyNoMoreInteractions(service, requestMock);
    }

    @Test
    void findByTemperatureUnitAndHost_whenRequest_thenReturnResponse() {
        var expected = ResponseEntity.ok(weatherDto);
        given(requestMock.getHeader(anyString())).willReturn(TEST_HEADER);
        given(service.findByUnitAndHost(TEST_TEMPERATURE_UNIT, TEST_HEADER)).willReturn(weatherDto);

        var result = victim.findByTemperatureUnitAndHost(TEST_TEMPERATURE_UNIT, requestMock);

        assertEquals(expected, result);
    }

}
