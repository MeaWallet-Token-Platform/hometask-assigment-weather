package com.paymentology.weather.infra.config;

import com.paymentology.weather.domain.weather.cache.CacheType;
import com.paymentology.weather.domain.weather.cache.DatabaseWeatherCache;
import com.paymentology.weather.domain.weather.cache.RedisCache;
import com.paymentology.weather.domain.weather.cache.WeatherCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "weather.cache")
public class WeatherCacheConfig {
    private static final Logger log = LoggerFactory.getLogger(WeatherCacheConfig.class);
    private Duration maxAge = Duration.ofMinutes(10);

    public Duration getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Duration maxAge) {
        this.maxAge = maxAge;
    }

    @Value("${weather.cache.type:DATABASE}")
    private String cacheTypeValue;

    @Bean
    @Primary
    public WeatherCache primaryWeatherCache(
            DatabaseWeatherCache databaseCache,
            RedisCache redisCache) {

        CacheType cacheType = CacheType.fromString(cacheTypeValue);
        log.info("Configuring weather cache with type: {}", cacheType);

        return switch (cacheType) {
            case REDIS -> redisCache;
            case DATABASE -> databaseCache;
        };
    }

}
