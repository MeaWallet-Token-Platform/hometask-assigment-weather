package com.paymentology.weather.domain.weather.client;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.weather.model;

import java.util.Optional;

public interface WeatherClient {
    /**
     * Fetches weather data from the API for a specific location
     *
     * @param geoLocation The location to fetch weather for
     * @param unit The desired temperature unit
     * @return Optional containing weather data if successful, empty otherwise
     */
    Optional<model.Weather> fetchWeather(GeoLocation geoLocation, model.TemperatureUnit unit);
}
