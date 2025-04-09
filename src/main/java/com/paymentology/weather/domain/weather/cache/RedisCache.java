package com.paymentology.weather.domain.weather.cache;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.weather.model;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class RedisCache implements WeatherCache {

    @Override
    public Optional<CachedWeatherEntry> find(GeoLocation location) {
        throw new NotImplementedException("Redis cache not implemented yet");
    }

    @Override
    public void save(GeoLocation location, model.Weather weather, Instant now) {
        throw new NotImplementedException("Redis cache not implemented yet");
    }
}
