package com.paymentology.weather.adapter.out.client;

import com.paymentology.weather.config.OpenMeteoConfig;
import com.paymentology.weather.domain.model.GeoLocation;
import com.paymentology.weather.domain.model.WeatherInfo;
import com.paymentology.weather.domain.model.WeatherInfo.Temperature;
import com.paymentology.weather.domain.model.WeatherInfo.TemperatureUnit;
import com.paymentology.weather.domain.model.WeatherInfo.Wind;
import com.paymentology.weather.domain.port.out.WeatherProvider;
import com.paymentology.weather.adapter.out.cache.WeatherCacheAdapter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenMeteoWeatherClientAdapter implements WeatherProvider {

    private final OpenMeteoConfig config;
    private final WebClient webClient;
    private final WeatherCacheAdapter cache;

    @Override
    public Mono<WeatherInfo.Weather> get(GeoLocation location, TemperatureUnit unit) {
        String cacheKey = buildKey(location, unit);

        return cache.get(cacheKey)
                .switchIfEmpty(fetchAndCache(location, unit, cacheKey))
                .onErrorResume(e -> {
                    log.warn("Weather fallback: trying cache for key {}", cacheKey);
                    return cache.get(cacheKey);
                });
    }

    @CircuitBreaker(name = "openMeteo", fallbackMethod = "fallback")
    private Mono<WeatherInfo.Weather> fetchAndCache(GeoLocation location, TemperatureUnit unit, String cacheKey) {
        String url = buildUrl(location, unit);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .mapNotNull(this::extractCurrentWeather)
                .map(current -> new WeatherInfo.Weather(
                        extractTemperature(current, unit),
                        extractWind(current),
                        Clock.systemUTC().instant()
                ))
                .doOnNext(w -> log.info("Weather fetched from Open-Meteo"))
                .flatMap(weather -> cache.put(cacheKey, weather).thenReturn(weather));
    }

    public Mono<WeatherInfo.Weather> fallback(GeoLocation location, TemperatureUnit unit, String cacheKey, Throwable ex) {
        log.error("Circuit breaker triggered for Open-Meteo API. Falling back to cache. Reason: {}", ex.getMessage());
        return cache.get(cacheKey);
    }

    private String buildUrl(GeoLocation location, TemperatureUnit unit) {
        String base = config.getUrl();
        String query = String.format(
                "?latitude=%f&longitude=%f&current_weather=true&temperature_unit=%s",
                location.latitude(), location.longitude(), unitName(unit));
        return base.endsWith("/") ? base.substring(0, base.length() - 1) + query : base + query;
    }

    private String buildKey(GeoLocation location, TemperatureUnit unit) {
        return "weather:" + location.latitude() + ":" + location.longitude() + ":" + unit.name().toLowerCase();
    }

    private String unitName(TemperatureUnit unit) {
        return switch (unit) {
            case Celsius -> "celsius";
            case Fahrenheit -> "fahrenheit";
            case Kelvin -> throw new IllegalArgumentException("Kelvin is not supported");
        };
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractCurrentWeather(Map<String, Object> response) {
        Object current = response.get("current_weather");
        if (current instanceof Map currentMap) return currentMap;
        throw new IllegalArgumentException("Missing or invalid 'current_weather'");
    }

    private Temperature extractTemperature(Map<String, Object> current, TemperatureUnit unit) {
        double value = doubleOf("temperature", current);
        return new Temperature(value, unit);
    }

    private Wind extractWind(Map<String, Object> current) {
        double speed = doubleOf("windspeed", current);
        double direction = doubleOf("winddirection", current);
        return new Wind(speed, direction);
    }

    private double doubleOf(String key, Map<String, Object> data) {
        Object value = data.get(key);
        return switch (value) {
            case Double d -> d;
            case Integer i -> i.doubleValue();
            case null -> throw new IllegalArgumentException("Missing value for key: " + key);
            default -> throw new IllegalArgumentException("Unexpected type for key " + key);
        };
    }
}
