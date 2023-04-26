package com.paymentology.weather.service.impl;

import com.paymentology.weather.RestTemplateService;
import com.paymentology.weather.constant.TemperatureUnit;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.OpenMeteoResponseDto;
import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.properties.OpenMeteoProperties;
import com.paymentology.weather.service.WeatherApiService;
import com.paymentology.weather.util.WeatherUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenMeteoWeatherService implements WeatherApiService {


    private final OpenMeteoProperties properties;
    private final RestTemplateService<OpenMeteoResponseDto> restTemplateService;
    private final WeatherUtil weatherUtil;


    @Override
    public Optional<WeatherDto> findByLocationAndUnit(GeoLocationDto geoLocation, TemperatureUnit requestUnit) {
        var apiUnit = weatherUtil.getApiUnit(requestUnit);

        var uri = weatherUtil.getCurrentWeatherUri(geoLocation, apiUnit, properties.getCurrentWeatherUrl());

/*        var openMeteoResponseDtoOptional = restTemplateService
                .getForObject(uri, OpenMeteoResponseDto.class);

        if (openMeteoResponseDtoOptional.isEmpty()) {
            return Optional.empty();
        }

        var openMeteoResponseDto = openMeteoResponseDtoOptional.get();

        weatherUtil.applyKelvinLogic(requestUnit, openMeteoResponseDto);

        return weatherUtil.createWeatherDto(openMeteoResponseDto, geoLocation, requestUnit);*/

        return restTemplateService.getForObject(uri, OpenMeteoResponseDto.class)
                .flatMap(dto -> weatherUtil.applyKelvinLogic(requestUnit, dto))
                .flatMap(dto -> weatherUtil.createWeatherDto(dto, geoLocation, requestUnit));
    }

}
