package com.paymentology.weather.domain.weather;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.weather.model.Weather;
import com.paymentology.weather.domain.weather.model.TemperatureUnit;

import java.time.Instant;
import java.util.Optional;

public interface WeatherService {
    Optional<Weather> get(GeoLocation location, TemperatureUnit unit, Instant createdAt);
}
