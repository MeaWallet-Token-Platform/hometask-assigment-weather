package com.paymentology.weather.service;

import com.paymentology.weather.constant.TemperatureUnit;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.WeatherDto;

import java.util.Optional;


public interface WeatherApiService {

    Optional<WeatherDto> findByLocationAndUnit(GeoLocationDto location, TemperatureUnit unit);

}
