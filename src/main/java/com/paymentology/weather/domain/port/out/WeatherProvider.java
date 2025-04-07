package com.paymentology.weather.domain.port.out;

import com.paymentology.weather.domain.model.GeoLocation;
import com.paymentology.weather.domain.model.WeatherInfo;
import com.paymentology.weather.domain.model.WeatherInfo.TemperatureUnit;
import reactor.core.publisher.Mono;

public interface WeatherProvider {
    Mono<WeatherInfo.Weather> get(GeoLocation location, TemperatureUnit unit);
}
