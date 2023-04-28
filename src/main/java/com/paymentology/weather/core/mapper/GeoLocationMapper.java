package com.paymentology.weather.core.mapper;

import com.paymentology.weather.core.model.GeoLocationDto;
import com.paymentology.weather.core.repository.entity.GeoLocationEntity;

public interface GeoLocationMapper {
    GeoLocationEntity dtoToEntity(GeoLocationDto requestDto);

    GeoLocationDto entityToDto(GeoLocationEntity savedEntity);

}
