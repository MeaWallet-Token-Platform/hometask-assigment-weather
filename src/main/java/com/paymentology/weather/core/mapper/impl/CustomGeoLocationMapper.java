package com.paymentology.weather.core.mapper.impl;

import com.paymentology.weather.core.mapper.GeoLocationMapper;
import com.paymentology.weather.core.model.GeoLocationDto;
import com.paymentology.weather.core.repository.entity.GeoLocationEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomGeoLocationMapper implements GeoLocationMapper {

    @Override
    public GeoLocationEntity dtoToEntity(GeoLocationDto requestDto) {
        return new GeoLocationEntity()
                .setId(requestDto.host())
                .setLatitude(requestDto.latitude())
                .setLongitude(requestDto.longitude());
    }

    @Override
    public GeoLocationDto entityToDto(GeoLocationEntity savedEntity) {
        return new GeoLocationDto(savedEntity.getId(), savedEntity.getLatitude(), savedEntity.getLongitude());
    }

}
