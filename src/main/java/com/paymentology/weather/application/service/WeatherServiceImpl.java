package com.paymentology.weather.application.service;

import com.paymentology.weather.domain.model.GeoLocation;
import com.paymentology.weather.domain.model.WeatherInfo;
import com.paymentology.weather.domain.model.WeatherInfo.TemperatureUnit;
import com.paymentology.weather.domain.port.in.WeatherUseCase;
import com.paymentology.weather.domain.port.out.WeatherProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherUseCase {

    private final WeatherProvider weatherProvider;

    @Override
    public Mono<WeatherInfo.Weather> get(GeoLocation location, TemperatureUnit unit) {
        return weatherProvider.get(location, unit)
                .doOnSubscribe(sub -> log.info("Fetching weather for: {} with unit: {}", location, unit))
                .doOnSuccess(weather -> log.info("Weather retrieved: {}", weather))
                .doOnError(error -> log.error("Failed to fetch weather data", error));
    }
}
