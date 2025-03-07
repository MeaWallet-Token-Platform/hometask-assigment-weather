package com.paymentology.weather.infra.weather;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.weather.Temperature;
import com.paymentology.weather.domain.weather.TemperatureUnit;
import com.paymentology.weather.domain.weather.Weather;
import com.paymentology.weather.domain.weather.Wind;
import com.paymentology.weather.infra.weather.dto.OpenMeteoResponse;
import com.paymentology.weather.infra.weather.dto.OpenMeteoResponse.CurrentWeather;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenMeteoWeatherServiceTest {

    @Mock
    private OpenMeteoConfig openMeteoConfig;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WeatherCacheService cacheService;

    @InjectMocks
    private OpenMeteoWeatherService victim;

    private final GeoLocation location = new GeoLocation(56.9496, 24.0978);
    private final String baseUrl = "https://api.open-meteo.com/v1/forecast";
    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2025-01-01T12:00:00Z"),
            ZoneId.of("UTC")
    );

    @Test
    void shouldReturnCachedWeatherIfAvailable() {
        Weather cachedWeather = createWeather(20.5, 5.0, 180.0);
        when(cacheService.getFromCache(location, TemperatureUnit.Celsius))
                .thenReturn(Optional.of(cachedWeather));

        Weather result = victim.get(location, TemperatureUnit.Celsius);

        assertEquals(cachedWeather, result);
    }

    @Test
    void shouldFetchFromApiAndCacheWhenNoCachedData() {
        when(openMeteoConfig.getUrl()).thenReturn(baseUrl);
        String expectedUrl = baseUrl + "?latitude=56.949600&longitude=24.097800&current_weather=true&temperature_unit=celsius";
        OpenMeteoResponse apiResponse = createApiResponse(20.5, 5.0, 180.0);
        Weather expectedWeather = createWeather(20.5, 5.0, 180.0);

        when(cacheService.getFromCache(location, TemperatureUnit.Celsius))
                .thenReturn(Optional.empty());
        when(restTemplate.getForObject(expectedUrl, OpenMeteoResponse.class))
                .thenReturn(apiResponse);

        Weather result = victim.get(location, TemperatureUnit.Celsius);

        assertEquals(expectedWeather.temperature().value(), result.temperature().value(), 0.01);
        assertEquals(expectedWeather.wind().speed(), result.wind().speed(), 0.01);
        assertEquals(expectedWeather.wind().direction(), result.wind().direction(), 0.01);
        verify(cacheService).saveToCache(location, result);
    }

    @Test
    void shouldConvertToKelvinWhenRequested() {
        when(openMeteoConfig.getUrl()).thenReturn(baseUrl);
        String expectedUrl = baseUrl + "?latitude=56.949600&longitude=24.097800&current_weather=true&temperature_unit=celsius";
        OpenMeteoResponse apiResponse = createApiResponse(20.5, 5.0, 180.0);
        double expectedKelvinTemp = 20.5 + 273.15;

        when(cacheService.getFromCache(location, TemperatureUnit.Kelvin))
                .thenReturn(Optional.empty());
        when(restTemplate.getForObject(expectedUrl, OpenMeteoResponse.class))
                .thenReturn(apiResponse);

        Weather result = victim.get(location, TemperatureUnit.Kelvin);

        assertEquals(expectedKelvinTemp, result.temperature().value(), 0.01);
        assertEquals(TemperatureUnit.Kelvin, result.temperature().unit());
    }

    @Test
    void shouldThrowExceptionWhenApiFailsAndNoCachedData() {
        when(openMeteoConfig.getUrl()).thenReturn(baseUrl);
        when(cacheService.getFromCache(location, TemperatureUnit.Celsius))
                .thenReturn(Optional.empty());
        when(restTemplate.getForObject(
                baseUrl + "?latitude=56.949600&longitude=24.097800&current_weather=true&temperature_unit=celsius",
                OpenMeteoResponse.class))
                .thenThrow(new RestClientException("API Error"));

        assertThrows(RuntimeException.class, () ->
            victim.get(location, TemperatureUnit.Celsius)
        );
    }

    private Weather createWeather(double temp, double windSpeed, double windDirection) {
        return new Weather(
                new Temperature(temp, TemperatureUnit.Celsius),
                new Wind(windSpeed, windDirection),
                fixedClock.instant()
        );
    }

    private OpenMeteoResponse createApiResponse(double temp, double windSpeed, double windDirection) {
        return new OpenMeteoResponse(
                new CurrentWeather(temp, windSpeed, windDirection)
        );
    }
}
