package com.paymentology.weather.mapper;

import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.repository.entity.WeatherEntity;

public interface WeatherMapper {
    WeatherDto entityToDto(WeatherEntity weatherEntity);

    WeatherEntity dtoToEntity(WeatherDto requestDto);

}
