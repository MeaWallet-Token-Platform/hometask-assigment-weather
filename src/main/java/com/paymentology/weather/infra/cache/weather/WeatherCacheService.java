package com.paymentology.weather.infra.cache.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentology.weather.domain.weather.Weather;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class WeatherCacheService {

    private static final Logger log = LoggerFactory.getLogger(WeatherCacheRepository.class);
    private final long weatherCacheEntryTtlMinutes;
    private final WeatherCacheRepository weatherCacheRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public WeatherCacheService(@Value("${cache.weather.ttl-minutes:1}") long weatherCacheEntryTtlMinutes,
                               WeatherCacheRepository weatherCacheRepository, ObjectMapper objectMapper) {
        this.weatherCacheEntryTtlMinutes = weatherCacheEntryTtlMinutes;
        this.weatherCacheRepository = weatherCacheRepository;
        this.objectMapper = objectMapper;
    }

    public Optional<Weather> getCached(double latitude, double longitude) {
        var identifier = new WeatherCacheEntityId(latitude, longitude);
        return weatherCacheRepository.findEntryByIdentifier(identifier, Instant.now()).
                        flatMap(e -> fromJson(e.getWeatherData(), identifier));
    }

    public void createOrUpdateCacheEntry(double latitude, double longitude, Weather weather) {
        if (Objects.isNull(weather)) {
           throw new IllegalArgumentException("Expected non null weather argument");
        }
        var weatherData = toJsonStringOrNull(weather);
        if (weatherData != null) {
            Instant expiredAt = Instant.now().plus(weatherCacheEntryTtlMinutes, ChronoUnit.MINUTES);
            weatherCacheRepository.save(new WeatherCacheEntity(latitude, longitude, weatherData, expiredAt));
        }
    }

    private Optional<Weather> fromJson(String json, WeatherCacheEntityId identifier) {
        try {
            return Optional.of(objectMapper.readValue(json, Weather.class));
        } catch (JsonProcessingException ex) {
            log.warn("Unable to convert weather data for cache entry with id [{}] from json", identifier);
            return Optional.empty();
        }
    }

    private String toJsonStringOrNull(Weather weather) {
        try {
            return objectMapper.writeValueAsString(weather);
        } catch (JsonProcessingException ex) {
            log.warn("Unable to convert to json weather data {}", weather);
            return null;
        }
    }
}
