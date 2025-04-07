package com.paymentology.weather.adapter.out.client;

import com.paymentology.weather.adapter.out.cache.WeatherCacheAdapter;
import com.paymentology.weather.config.OpenMeteoConfig;
import com.paymentology.weather.domain.model.GeoLocation;
import com.paymentology.weather.domain.model.WeatherInfo;
import com.paymentology.weather.domain.model.WeatherInfo.TemperatureUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.Mockito.*;

class OpenMeteoWeatherClientAdapterTest {

    private OpenMeteoConfig config;
    private WebClient webClient;
    private WeatherCacheAdapter cache;
    private OpenMeteoWeatherClientAdapter adapter;

    private GeoLocation location = new GeoLocation(40.4093, 49.8671);

    @BeforeEach
    void setUp() {
        config = mock(OpenMeteoConfig.class);
        when(config.getUrl()).thenReturn("https://api.open-meteo.com/v1/forecast");

        webClient = WebClient.builder().baseUrl("https://api.open-meteo.com").build();
        cache = mock(WeatherCacheAdapter.class);

        adapter = new OpenMeteoWeatherClientAdapter(config, webClient, cache);
    }

    @Test
    void get_shouldReturnCachedDataIfExists() {
        WeatherInfo.Weather cachedWeather = new WeatherInfo.Weather(
                new WeatherInfo.Temperature(25.0, TemperatureUnit.Celsius),
                new WeatherInfo.Wind(5.5, 180),
                Instant.now()
        );

        when(cache.get(anyString())).thenReturn(Mono.just(cachedWeather));

        StepVerifier.create(adapter.get(location, TemperatureUnit.Celsius))
                .expectNextMatches(w -> w.temperature().value() == 25.0)
                .verifyComplete();

        verify(cache, times(1)).get(anyString());
        verifyNoMoreInteractions(cache);
    }

    @Test
    void get_shouldFallbackToCacheWhenFetchFails() {
        when(cache.get(anyString())).thenReturn(Mono.empty()).thenReturn(Mono.just(new WeatherInfo.Weather(
                new WeatherInfo.Temperature(22.0, TemperatureUnit.Celsius),
                new WeatherInfo.Wind(4.5, 150),
                Instant.now()
        )));

        StepVerifier.create(adapter.get(location, TemperatureUnit.Celsius))
                .expectNextMatches(w -> w.temperature().value() == 22.0)
                .verifyComplete();
    }

    @Test
    void fallback_shouldReturnCachedData() {
        WeatherInfo.Weather fallbackWeather = new WeatherInfo.Weather(
                new WeatherInfo.Temperature(21.0, TemperatureUnit.Celsius),
                new WeatherInfo.Wind(3.5, 130),
                Instant.now()
        );

        when(cache.get(anyString())).thenReturn(Mono.just(fallbackWeather));

        StepVerifier.create(adapter.fallback(location, TemperatureUnit.Celsius, "weather:40.4093:49.8671:celsius", new RuntimeException("API down")))
                .expectNextMatches(w -> w.temperature().value() == 21.0)
                .verifyComplete();
    }
}
