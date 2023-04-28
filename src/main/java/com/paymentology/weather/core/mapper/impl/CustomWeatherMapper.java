package com.paymentology.weather.core.mapper.impl;

import com.paymentology.weather.core.model.TemperatureDto;
import com.paymentology.weather.core.model.WeatherDto;
import com.paymentology.weather.core.model.WindDto;
import com.paymentology.weather.core.mapper.WeatherMapper;
import com.paymentology.weather.core.repository.entity.WeatherEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomWeatherMapper implements WeatherMapper {

    @Override
    public WeatherDto entityToDto(WeatherEntity weatherEntity) {
        return new WeatherDto(
                weatherEntity.getId(),
                new TemperatureDto(weatherEntity.getTemperature(), weatherEntity.getTemperatureUnit()),
                new WindDto(weatherEntity.getWindSpeed(), weatherEntity.getWindDirection()),
                weatherEntity.getCreated());
    }

    @Override
    public WeatherEntity dtoToEntity(WeatherDto requestDto) {
        return new WeatherEntity()
                .setId(requestDto.host())
                .setTemperature(requestDto.temperature().value())
                .setTemperatureUnit(requestDto.temperature().unit())
                .setWindSpeed(requestDto.wind().speed())
                .setWindDirection(requestDto.wind().direction())
                .setCreated(requestDto.timestamp());
    }

}
