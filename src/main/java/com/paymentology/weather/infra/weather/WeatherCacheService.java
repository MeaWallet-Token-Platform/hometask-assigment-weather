package com.paymentology.weather.infra.weather;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.weather.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Optional;


@Service
public class WeatherCacheService {
    private static final String CACHE_KEY_FORMAT = "weather:%f:%f:%s";
    private static final Duration CACHE_TTL = Duration.ofSeconds(30);
    private static final Logger log = LoggerFactory.getLogger(WeatherCacheService.class);

    private final RedisTemplate<String, WeatherCacheEntity> redisTemplate;

    @Autowired
    public WeatherCacheService(RedisTemplate<String, WeatherCacheEntity> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<Weather> getFromCache(GeoLocation location, TemperatureUnit unit) {
        String key = generateKey(location, unit);
        try {
            WeatherCacheEntity cachedEntity = redisTemplate.opsForValue().get(key);
            if (cachedEntity != null) {
                // Extend TTL on cache hit
                redisTemplate.expire(key, CACHE_TTL);
                log.info("Cache hit for location: {}, {}, extending TTL",
                        location.latitude(), location.longitude());
                return Optional.of(cachedEntity.toDomain());
            }
            log.info("Cache miss for location: {}, {}",
                    location.latitude(), location.longitude());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error accessing cache for location: {}, {}: {}",
                    location.latitude(), location.longitude(), e.getMessage());
            return Optional.empty();
        }
    }

    public void saveToCache(GeoLocation location, Weather weather) {
        String key = generateKey(location, weather.temperature().unit());
        try {
            WeatherCacheEntity entity = new WeatherCacheEntity(
                    location.latitude(),
                    location.longitude(),
                    weather
            );
            redisTemplate.opsForValue().set(
                    key,
                    entity,
                    CACHE_TTL
            );
            log.debug("Saved weather to cache for location: {}, {} with {}s TTL",
                    location.latitude(), location.longitude(), CACHE_TTL.getSeconds());
        } catch (Exception e) {
            log.error("Error saving to cache for location: {}, {}: {}",
                    location.latitude(), location.longitude(), e.getMessage());
        }
    }

    private String generateKey(GeoLocation location, TemperatureUnit unit) {
        return String.format(CACHE_KEY_FORMAT,
                location.latitude(),
                location.longitude(),
                unit.toString().toLowerCase()
        );
    }
} 