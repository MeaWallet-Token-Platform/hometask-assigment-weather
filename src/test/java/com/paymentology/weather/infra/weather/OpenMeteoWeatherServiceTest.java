package com.paymentology.weather.infra.weather;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.weather.cache.WeatherCache;
import com.paymentology.weather.infra.config.WeatherCacheConfig;
import com.paymentology.weather.domain.weather.client.WeatherClient;
import com.paymentology.weather.domain.weather.model.Temperature;
import com.paymentology.weather.domain.weather.model.TemperatureUnit;
import com.paymentology.weather.domain.weather.model.Weather;
import com.paymentology.weather.domain.weather.model.Wind;
import com.paymentology.weather.infra.weather.service.OpenMeteoWeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OpenMeteoWeatherServiceTest {

    @Mock
    private WeatherClient weatherClient;

    @Mock
    private WeatherCache weatherCache;

    @Mock
    private WeatherCacheConfig cacheConfig;

    private OpenMeteoWeatherService weatherService;
    private final Instant fixedInstant = Instant.parse("2025-03-28T12:00:00Z");
    private final GeoLocation testLocation = new GeoLocation(51.5074, -0.1278);

    @BeforeEach
    void setUp() {
        weatherService = new OpenMeteoWeatherService(
                weatherClient,
                weatherCache,
                cacheConfig);
    }

    @Test
    void shouldReturnNewDataWhenApiCallSucceeds() {
        Weather expectedWeather = new Weather(
                new Temperature(10.5, TemperatureUnit.Celsius),
                new Wind(5.3, 270.0)
        );

        when(weatherClient.fetchWeather(eq(testLocation), eq(TemperatureUnit.Celsius)))
                .thenReturn(Optional.of(expectedWeather));

        Optional<Weather> result = weatherService.get(testLocation, TemperatureUnit.Celsius, fixedInstant);

        assertTrue(result.isPresent());
        assertEquals(expectedWeather, result.get());

        verify(weatherCache).save(eq(testLocation), eq(expectedWeather), eq(fixedInstant));
    }

    @Test
    void shouldReturnValidCachedDataWhenApiCallFails() {
        when(weatherClient.fetchWeather(any(), any())).thenReturn(Optional.empty());

        Weather cachedWeather = new Weather(
                new Temperature(15.5, TemperatureUnit.Celsius),
                new Wind(4.2, 180.0)
        );

        WeatherCache.CachedWeatherEntry validCacheEntry =
                new WeatherCache.CachedWeatherEntry(cachedWeather, fixedInstant.minus(Duration.ofMinutes(5)));

        when(weatherCache.find(eq(testLocation))).thenReturn(Optional.of(validCacheEntry));
        when(cacheConfig.getMaxAge()).thenReturn(Duration.ofMinutes(10));

        Optional<Weather> result = weatherService.get(testLocation, TemperatureUnit.Celsius, fixedInstant);

        assertTrue(result.isPresent());
        assertEquals(cachedWeather, result.get());

        verify(weatherCache, never()).save(any(), any(), any());
    }

    @Test
    void shouldReturnStaleCachedDataWhenApiFailsAndNothingValidExists() {
        when(weatherClient.fetchWeather(any(), any())).thenReturn(Optional.empty());

        Weather staleWeather = new Weather(
                new Temperature(20.0, TemperatureUnit.Celsius),
                new Wind(3.0, 90.0)
        );

        WeatherCache.CachedWeatherEntry staleCacheEntry =
                new WeatherCache.CachedWeatherEntry(staleWeather, fixedInstant.minus(Duration.ofHours(2)));

        when(weatherCache.find(eq(testLocation))).thenReturn(Optional.of(staleCacheEntry));
        when(cacheConfig.getMaxAge()).thenReturn(Duration.ofMinutes(10));

        Optional<Weather> result = weatherService.get(testLocation, TemperatureUnit.Celsius, fixedInstant);

        assertTrue(result.isPresent());
        assertEquals(staleWeather, result.get());
    }

    @Test
    void shouldConvertTemperatureUnitWhenCachedUnitDiffersFromRequested() {
        when(weatherClient.fetchWeather(any(), any())).thenReturn(Optional.empty());

        Weather celsiusWeather = new Weather(
                new Temperature(20.0, TemperatureUnit.Celsius),
                new Wind(3.0, 90.0)
        );

        WeatherCache.CachedWeatherEntry cacheEntry =
                new WeatherCache.CachedWeatherEntry(celsiusWeather, fixedInstant);

        when(weatherCache.find(eq(testLocation))).thenReturn(Optional.of(cacheEntry));

        Optional<Weather> result = weatherService.get(testLocation, TemperatureUnit.Fahrenheit, fixedInstant);

        assertTrue(result.isPresent());
        Weather weather = result.get();
        assertEquals(TemperatureUnit.Fahrenheit, weather.temperature().unit());
        assertEquals(68.0, weather.temperature().value(), 0.1); // 20°C should convert to 68°F
    }

    @Test
    void shouldReturnEmptyWhenApiFailsAndNoCacheExists() {
        when(weatherClient.fetchWeather(any(), any())).thenReturn(Optional.empty());
        when(weatherCache.find(eq(testLocation))).thenReturn(Optional.empty());

        Optional<Weather> result = weatherService.get(testLocation, TemperatureUnit.Celsius, fixedInstant);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldAlwaysTryNewDataBeforeUsingCache() {
        Weather freshWeather = new Weather(
                new Temperature(10.5, TemperatureUnit.Celsius),
                new Wind(5.3, 270.0)
        );

        when(weatherClient.fetchWeather(eq(testLocation), eq(TemperatureUnit.Celsius)))
                .thenReturn(Optional.of(freshWeather));

        Optional<Weather> result = weatherService.get(testLocation, TemperatureUnit.Celsius, fixedInstant);

        assertTrue(result.isPresent());
        assertEquals(freshWeather, result.get());

        verify(weatherClient).fetchWeather(eq(testLocation), eq(TemperatureUnit.Celsius));
        verify(weatherCache).save(eq(testLocation), eq(freshWeather), eq(fixedInstant));
    }
}
