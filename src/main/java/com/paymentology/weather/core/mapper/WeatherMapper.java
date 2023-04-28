package com.paymentology.weather.core.mapper;

import com.paymentology.weather.core.model.WeatherDto;
import com.paymentology.weather.core.repository.entity.WeatherEntity;

public interface WeatherMapper {
    WeatherDto entityToDto(WeatherEntity weatherEntity);

    WeatherEntity dtoToEntity(WeatherDto requestDto);

}
