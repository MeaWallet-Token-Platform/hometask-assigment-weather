package com.paymentology.weather.service.impl;

import com.paymentology.weather.mapper.GeoLocationMapper;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.repository.GeoLocationRepository;
import com.paymentology.weather.repository.entity.GeoLocationEntity;
import com.paymentology.weather.service.GeoLocationEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeoLocationEntityServiceImpl implements GeoLocationEntityService {

    private static final String CLASS_NAME = GeoLocationEntity.class.getSimpleName();

    private static final String GEO_LOCATION_SAVED_WITH_ID = CLASS_NAME + " saved with id: ";
    private static final String GEO_LOCATION_FOUND_FOR_HOST = CLASS_NAME + " found for host: ";

    private final GeoLocationRepository repository;
    private final GeoLocationMapper mapper;

    @Override
    @Cacheable(value = "geoLocationCache")
    public Optional<GeoLocationDto> findByHost(String host) {
        var dto = repository.findDtoByHost(host);
        dto.ifPresent(geoLocationDto -> log.info(GEO_LOCATION_FOUND_FOR_HOST + geoLocationDto.host()));
        return dto;
    }

    @Override
    public GeoLocationDto save(GeoLocationDto requestDto) {
        var requestEntity = mapper.dtoToEntity(requestDto);
        var savedEntity = repository.save(requestEntity);
        log.info(GEO_LOCATION_SAVED_WITH_ID + savedEntity.getId());
        return mapper.entityToDto(savedEntity);
    }
}
