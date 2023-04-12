package com.paymentology.weather.mapper.impl;

import com.paymentology.weather.mapper.WeatherMapper;
import com.paymentology.weather.model.TemperatureDto;
import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.model.WindDto;
import com.paymentology.weather.repository.entity.WeatherEntity;
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
