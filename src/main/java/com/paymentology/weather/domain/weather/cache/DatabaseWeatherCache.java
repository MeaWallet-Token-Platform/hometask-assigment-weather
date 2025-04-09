package com.paymentology.weather.domain.weather.cache;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.weather.model.Weather;
import com.paymentology.weather.infra.weather.repository.WeatherCacheEntry;
import com.paymentology.weather.infra.weather.repository.WeatherCacheRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

/**
 * Database implementation of the WeatherCache interface using JPA.
 */
@Component
public class DatabaseWeatherCache implements WeatherCache {
    private static final Logger log = LoggerFactory.getLogger(DatabaseWeatherCache.class);

    private final WeatherCacheRepository repository;

    public DatabaseWeatherCache(WeatherCacheRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<CachedWeatherEntry> find(GeoLocation location) {
        log.debug("Looking for cached weather for location: {}", location);
        return repository.findLatestByLocation(
                        location.latitude(), location.longitude())
                .map(entry -> new CachedWeatherEntry(
                        entry.toWeather(),
                        entry.getCreatedAt()
                ));
    }

    @Transactional
    @Override
    public void save(GeoLocation location, Weather weather, Instant createdAt) {
        log.info("Caching weather data to database for location: {}", location);
        repository.save(WeatherCacheEntry.fromWeather(location, weather, createdAt));
    }
}
