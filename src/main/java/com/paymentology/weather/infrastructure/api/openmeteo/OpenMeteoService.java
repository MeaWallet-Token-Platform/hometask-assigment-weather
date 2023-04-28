package com.paymentology.weather.infrastructure.api.openmeteo;

import com.paymentology.weather.core.constant.TemperatureUnit;
import com.paymentology.weather.core.model.WeatherDto;
import com.paymentology.weather.infrastructure.service.RestTemplateService;
import com.paymentology.weather.core.model.GeoLocationDto;
import com.paymentology.weather.core.service.WeatherApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenMeteoService implements WeatherApiService {


    private final OpenMeteoProperties properties;
    private final RestTemplateService<OpenMeteoResponseDto> restTemplateService;
    private final WeatherUtil weatherUtil;


    @Override
    public Optional<WeatherDto> findByLocationAndUnit(GeoLocationDto geoLocation, TemperatureUnit requestUnit) {
        var apiUnit = weatherUtil.getApiUnit(requestUnit);

        var uri = weatherUtil.getCurrentWeatherUri(geoLocation, apiUnit, properties.getCurrentWeatherUrl());

        return restTemplateService.getForObject(uri, OpenMeteoResponseDto.class)
                .map(dto -> weatherUtil.applyKelvinLogic(requestUnit, dto))
                .map(dto -> weatherUtil.createWeatherDto(dto, geoLocation, requestUnit));
    }

}
