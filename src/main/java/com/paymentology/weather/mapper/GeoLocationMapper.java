package com.paymentology.weather.mapper;

import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.repository.entity.GeoLocationEntity;

public interface GeoLocationMapper {
    GeoLocationEntity dtoToEntity(GeoLocationDto requestDto);

    GeoLocationDto entityToDto(GeoLocationEntity savedEntity);
}
