package com.paymentology.weather.core.service;

import com.paymentology.weather.core.constant.TemperatureUnit;
import com.paymentology.weather.core.model.GeoLocationDto;
import com.paymentology.weather.core.model.WeatherDto;

import java.util.Optional;


public interface WeatherApiService {

    Optional<WeatherDto> findByLocationAndUnit(GeoLocationDto location, TemperatureUnit unit);

}
