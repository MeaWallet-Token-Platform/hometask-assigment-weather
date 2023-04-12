package com.paymentology.weather.service;

import com.paymentology.weather.constant.TemperatureUnit;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.WeatherDto;

import java.util.Optional;

public interface WeatherEntityService {
    Optional<WeatherDto> findByLocationAndUnit(GeoLocationDto geoLocationDto, TemperatureUnit unit);

    WeatherDto save(WeatherDto weatherDto);
}
