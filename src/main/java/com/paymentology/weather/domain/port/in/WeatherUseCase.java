package com.paymentology.weather.domain.port.in;

import com.paymentology.weather.domain.model.GeoLocation;
import com.paymentology.weather.domain.model.WeatherInfo;
import reactor.core.publisher.Mono;

public interface WeatherUseCase {
    /**
     * Retrieves the weather data for a given location and temperature unit.
     *
     * @param location the geolocation (latitude, longitude)
     * @param unit the temperature unit (Celsius, Fahrenheit)
     * @return Mono emitting the weather result or error if failed
     */
    Mono<WeatherInfo.Weather> get(GeoLocation location, WeatherInfo.TemperatureUnit unit);
}
