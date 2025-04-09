package com.paymentology.weather.infra.weather.service;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.weather.WeatherService;
import com.paymentology.weather.domain.weather.cache.WeatherCache;
import com.paymentology.weather.infra.config.WeatherCacheConfig;
import com.paymentology.weather.domain.weather.client.WeatherClient;
import com.paymentology.weather.domain.weather.model.TemperatureUnit;
import com.paymentology.weather.domain.weather.model.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class OpenMeteoWeatherService implements WeatherService {

    private static final Logger log = LoggerFactory.getLogger(OpenMeteoWeatherService.class);

    private final WeatherClient weatherClient;
    private final WeatherCache weatherCache;
    private final WeatherCacheConfig cacheConfig;


    @Autowired
    public OpenMeteoWeatherService(
            WeatherClient weatherClient,
            //@Qualifier("databaseWeatherCache") WeatherCache weatherCache,
            WeatherCache weatherCache,
            WeatherCacheConfig cacheConfig) {
        this.weatherClient = weatherClient;
        this.weatherCache = weatherCache;
        this.cacheConfig = cacheConfig;
    }

    @Override
    public Optional<Weather> get(GeoLocation geoLocation, TemperatureUnit unit, Instant now) {
        return weatherClient.fetchWeather(geoLocation, unit)
                .map(weather -> {
                    weatherCache.save(geoLocation, weather, now);
                    log.info("Fetched and cached fresh weather data for location: {}", geoLocation);
                    return weather;
                })
                .or(() -> weatherCache.find(geoLocation)
                        .stream().peek(entry -> entry.log(cacheConfig.getMaxAge(), geoLocation))
                        .map(cachedEntry -> cachedEntry.weather().withTemperatureUnit(unit)).findFirst()
                );
    }
}

