package com.paymentology.weather.application.service;

import com.paymentology.weather.domain.model.GeoLocation;
import com.paymentology.weather.domain.model.WeatherInfo;
import com.paymentology.weather.domain.model.WeatherInfo.Temperature;
import com.paymentology.weather.domain.model.WeatherInfo.TemperatureUnit;
import com.paymentology.weather.domain.model.WeatherInfo.Wind;
import com.paymentology.weather.domain.port.out.WeatherProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.Mockito.*;

class WeatherServiceImplTest {

    private WeatherProvider weatherProvider;
    private WeatherServiceImpl weatherService;

    @BeforeEach
    void setUp() {
        weatherProvider = mock(WeatherProvider.class);
        weatherService = new WeatherServiceImpl(weatherProvider);
    }

    @Test
    void get_shouldReturnWeather_whenProviderSucceeds() {
        GeoLocation location = new GeoLocation(40.0, 49.0);
        TemperatureUnit unit = TemperatureUnit.Celsius;

        WeatherInfo.Weather expected = new WeatherInfo.Weather(
                new Temperature(25.5, unit),
                new Wind(12.0, 180.0),
                Instant.now()
        );

        when(weatherProvider.get(location, unit)).thenReturn(Mono.just(expected));

        StepVerifier.create(weatherService.get(location, unit))
                .expectNextMatches(actual ->
                        actual.temperature().value() == expected.temperature().value()
                                && actual.wind().speed() == expected.wind().speed()
                                && actual.wind().direction() == expected.wind().direction()
                )
                .verifyComplete();

        verify(weatherProvider).get(location, unit);
    }

    @Test
    void get_shouldReturnError_whenProviderFails() {
        GeoLocation location = new GeoLocation(50.0, 60.0);
        TemperatureUnit unit = TemperatureUnit.Fahrenheit;
        RuntimeException error = new RuntimeException("Weather service down");

        when(weatherProvider.get(location, unit)).thenReturn(Mono.error(error));

        StepVerifier.create(weatherService.get(location, unit))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Weather service down")
                )
                .verify();

        verify(weatherProvider).get(location, unit);
    }
}
